import numpy as np
import urllib.request
import zipfile
import os
from tqdm.notebook import tqdm
import pandas as pd
import pyarrow.parquet as pq
import matplotlib.pyplot as plt

MILES_TO_KM = 1.60934
YEARS = [2019, 2022]

def load_table(year, month, sampling = 100):
    """
    Funció que llegeix les dades descarregades i les converteix a un DataFrame
    """
    data = pq.read_table(f'data/{year}/{str(month).zfill(2)}.parquet').to_pandas()
    required_data = ['tpep_pickup_datetime',
                'tpep_dropoff_datetime',
                'passenger_count',
                'trip_distance',
                'PULocationID',
                'DOLocationID',
                'payment_type',
                'fare_amount',
                'total_amount']
    return data[required_data][::sampling]


def clean_data(data, year, month):
    """
    Funció que neteja (una mostra de) les dades per un mes donat.
    
    Parameters:
    - data (DataFrame): El DataFrame a filtrar.
    - year (int): Any que estem filtrant.
    - month (int): Mes que estem estudiant.

    Returns:
    - DataFrame: El DataFrame filtrat o original si es supera el límit de retallada.
    """

    # Carreguem les dades
    data = load_table(year, month)

    # 0. Eliminem els duplicats
    data = data.drop_duplicates()

    # 1. Presència de missing data (camps/columnes de les dades sense valor).
    data = data.dropna()

    # 2. L'hora de recollida és posterior a la finalització del trajecte.
    data['tpep_pickup_datetime'] = pd.to_datetime(data['tpep_pickup_datetime'])
    data['tpep_dropoff_datetime'] = pd.to_datetime(data['tpep_dropoff_datetime'])
    data = data[data['tpep_pickup_datetime'] < data['tpep_dropoff_datetime']]

    # 3. Les dades s'importen per mes i any. Són coherents els valors que contenen les dades?
    data = data[data['tpep_pickup_datetime'].dt.year == year]
    data = data[data['tpep_pickup_datetime'].dt.month == month]

    # 4. Hi ha viatges amb un nombre absurd de passatgers? (considerem que poden haver fins a 7 passatgers a les furgonetes)
    data = data[(data['passenger_count'] >= 0) & (data['passenger_count'] < 7)]

    # 5. Hi ha pagaments negatius o nuls?
    data = data[(data['fare_amount'] > 0) & (data['total_amount'] > 0) & (data['total_amount'] > data['fare_amount'])]

    # 6. Els tipus de pagament són vàlids?
    valid_payment_types = [1, 2, 3, 4]
    data = data[(data['payment_type'].isin(valid_payment_types))]

    # 7. Comprovem que el PULocationID i el DOLocationID siguin correctes
    data = data[(data['PULocationID'] >= 1) & (data['PULocationID'] <= 263) & (data['DOLocationID'] >= 1) & (data['DOLocationID'] <= 263)]

    # Fem reset del índex per tenir-ho actualitzat
    data.reset_index(drop=True, inplace=True)

    return data


def post_processing(df):
    """
    Funció on implementar qualsevol tipus de postprocessament necessari.

    Parameters:
    - df (DataFrame): El DataFrame amb el que treballem.

    Returns:
    - DataFrame: El DataFrame amb les noves columnes.
    """

    data = df.copy()

    # 0. Hi ha viatges massa llargs o massa curts?
    # Calcular el temps del viatge en hores (ho podem fer perque ja hem filtrat
    # les diferències negatives) i filtrem aquells que no tinguin sentit
    # com ara aquells que duren menys d'un minut (0.0166667 hores) i 4 hores
    data['travel_time'] = data['tpep_dropoff_datetime'] - data['tpep_pickup_datetime']
    data['travel_time'] = data['travel_time'].dt.total_seconds() / 3600
    data = data[(data['travel_time'] > 0.016) & (data['travel_time'] <= 4)]

    # 1. Passem les milles a km
    data['trip_distance'] = data['trip_distance'] * MILES_TO_KM

    # 2. Eliminem els viatges amb distancia irreal (superior a 150 km o inferior a 100 m)
    data = data[(data['trip_distance'] <= 150) & (data['trip_distance'] > 0.1)]

    # 3. Filtrem per les velocitats mitjanes
    data['avg_speed'] = data['trip_distance'] / data['travel_time']
    data = data[data['avg_speed'] < 65 * MILES_TO_KM]

    # 4. Filtrem pel preu per km
    data['price_km'] = data['total_amount'] / data['trip_distance']

    # 5. Filtrem pel preu per minut
    data['price_min'] = data['total_amount'] / (data['travel_time'] * 60)

    # 6. Afegim una columna hour
    data['pickup_hour'] = data['tpep_pickup_datetime'].dt.hour
    data['dropoff_hour'] = data['tpep_dropoff_datetime'].dt.hour

    # 7. Afegim una columna day
    data['pickup_day'] = data['tpep_pickup_datetime'].dt.day
    data['dropoff_day'] = data['tpep_dropoff_datetime'].dt.day

    # 8. Afegim una columna day of the week
    data['pickup_dow'] = data['tpep_pickup_datetime'].dt.dayofweek
    data['dropoff_dow'] = data['tpep_dropoff_datetime'].dt.dayofweek

    # 9. Afegim una columna week of year
    data['pickup_week'] = data['tpep_pickup_datetime'].dt.isocalendar().week
    data['dropoff_week'] = data['tpep_dropoff_datetime'].dt.isocalendar().week

    # 10. Afegim una columna month
    data['month'] = data['tpep_pickup_datetime'].dt.month

    # 11. Afegim una columna year
    data['year'] = data['tpep_pickup_datetime'].dt.year

    # 12. Afegim una columna quarter
    data['quarter'] = data['tpep_pickup_datetime'].dt.quarter

    # 13. Fem reset del índex per tenir-ho actualitzat
    data.reset_index(drop=True, inplace=True)

    return data


def bar_plot(df, column, xlabel, ylabel, title):
    """
    Funció que crea una figura de barres a partir del dataframe i
    la columna que conté la informació

    Parameters:
    - df (DataFrame): El DataFrame amb el que treballem.
    - column (str): Columna que volem filtrar.
    - xlabel (str): Nom de l'eix horitzonal.
    - ylabel (str): Nom de l'eix vertcial.
    - title (str): Títol de la taula.
    """

    # Comptem els viatges
    trips = df[column].value_counts().sort_index()
    # Alternativament, si volguéssim podríem fer servir
    # trips = df.groupby(column).size()

    # Creem el gràfic
    plt.figure(figsize=(6,6))
    plt.bar(trips.index, trips.values, color='skyblue', alpha=0.7)

    # Anomenem els eixos segons els valors passats per paràmetre
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.title(title)

    # Mostrem el temps a l'eix x
    plt.xticks(trips.index)

    plt.show()


def passengers_taxi_year(df, ylim, xlabel, ylabel, title, norm = False):
    """
    Funció que visualitza quants passatgers hi ha per taxi i per any

    Parameters:
    - df (DataFrame): El DataFrame amb el que treballem.
    - ylim (range): Límit que tindran els nostres valors.
    - xlabel (str): Nom de l'eix horitzonal.
    - ylabel (str): Nom de l'eix vertcial.
    - title (str): Títol de la taula.
    - norm (boolean): Indicador sobre si hem de normalitzar o no les dades.
    """

    fig, axs = plt.subplots(1, 3, figsize=(30, 10))
    axs = axs.ravel()

    for i, year in enumerate(YEARS):
        # Filtrem el DataFrame per un any en particular
        yearly_trips = df[df['year'] == year]
        trips_per_passenger = yearly_trips['passenger_count'].value_counts(normalize=norm).sort_index()

        # Mostrem la informació
        bars = axs[i].bar(trips_per_passenger.index, trips_per_passenger.values, color='skyblue', alpha=0.7)
        axs[i].set_xticks(trips_per_passenger.index)
        axs[i].set_title(f'{title} (any {year})')
        axs[i].set_xlabel(xlabel)
        axs[i].set_ylabel(ylabel)
        axs[i].set_ylim(ylim)

        for bar in bars:
            if bar.get_height() < ylim[1]:
                yval = bar.get_height()
                axs[i].text(bar.get_x() + bar.get_width()/2, yval + 0.01, round(yval, 3), ha='center', va='bottom')

    plt.show()


def column_descriptors(df, column, stat):
  """
  Funció que visualitza dades estadístiques de cada any segons el mes

  Parameters:
  - df (DataFrame): El DataFrame amb el que treballem.
  - colum (str): Columna que volem estudiar.
  - stat (str): Dada estadística que volem visualitzar.
  """

  fig, axes = plt.subplots(3, 1, figsize=(15, 10))
  fig.suptitle(column, fontsize=16)
  axes = axes.ravel()

  for i, year in enumerate(YEARS):

    yearly_data = df[df['year'] == year]

    if column not in yearly_data.columns:
        raise KeyError("This DataFrame does not contain such column")

    # Utilitzem group by per agrupar les dades pel mes
    monthly_data = yearly_data.groupby('month')[column]
    indicators = monthly_data.describe()

    if stat not in indicators.keys():
        raise KeyError("Invalid key")

    indicator = indicators[stat]

    # Dibuixem els gràfics
    bars = axes[i].bar(indicator.index, indicator.values, color='skyblue', alpha=0.7)
    axes[i].set_xticks(range(1, 13))
    axes[i].set_title(f'{stat} ({year})')
    axes[i].set_xlabel('Month')
    axes[i].set_ylabel(f'{stat}')

    # Afegim el valor adalt de la barra
    for bar in bars:
        yval = bar.get_height()
        axes[i].text(bar.get_x() + bar.get_width()/2, yval + 0.01, round(yval, 3), ha='center', va='bottom')

    plt.tight_layout()
    plt.show()


def passenger_number_evolution(df, column, ylim, xlabel, ylabel, title, norm = False):
    """
    Evolució d'algunes dades estadístiques

    Parameters:
    - df (DataFrame): El DataFrame amb el que treballem.
    - column (str): Columna que volem estudiar.
    - ylim (range): Límit que tindran els nostres valors.
    - xlabel (str): Nom de l'eix horitzonal.
    - ylabel (str): Nom de l'eix vertcial.
    - title (str): Títol de la taula.
    - norm (boolean): Indicador sobre si hem de normalitzar o no les dades.
    """

    plt.figure(figsize=(14, 6))

    # Definir una llista per emmagatzemar les variacions percentuals
    first_year_data = None

    for i, year in enumerate(YEARS):
        # Filtrem el DataFrame per un any en particular
        yearly_trips = df[df['year'] == year]

        if column not in yearly_trips.columns:
            raise KeyError("This DataFrame does not contain such column")

        evolution = yearly_trips[column].value_counts(normalize=norm).sort_index()

        if first_year_data is None:
            # Per al primer any, establir la variació a 0
            percentage_change = evolution * 0
            # Actualitzar les dades de l'any anterior per a la propera iteració
            first_year_data = evolution

        else:
            # Calcular la variació percentual respecte a l'any anterior
            percentage_change = ((evolution - first_year_data) / first_year_data)

            # Mostrem la informació
            bars = plt.bar(percentage_change.index + 0.3*(i-1), percentage_change.values, label=f'{year}', width=0.3, alpha=0.7)
            plt.xticks(evolution.index)

            # Afegim el valor adalt de la barra
            for bar in bars:
                yval = bar.get_height()
                plt.text(bar.get_x() + bar.get_width()/2, yval + 0.01, round(yval, 3), ha='center', va='bottom')

    plt.axhline(0, color='black', linewidth=0.7, linestyle='-')
    plt.legend()
    plt.title(title)
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.ylim(ylim)
    plt.show()


def visualize_trips(df, columns, title, xlabel, ylabel):
    """
    Funció que visualitza els viatges per diferents agregacions de dades

    Parameters:
    - df (DataFrame): El DataFrame amb el que treballem.
    - columns (list): Columnes que comparem.
    - title (str): Títol de la taula.
    - xlabel (str): Nom de l'eix horitzonal.
    - ylabel (str): Nom de l'eix vertcial.
    """

    if not isinstance(columns, list):
        print("Ens estàs passant unes columnes en format invàlid")
        return

    fig, axes = plt.subplots(len(columns), 1, figsize=(15, 10))

    # En cas de tenir una llista, iterem pels seus valors
    for i, column in enumerate(columns):
        for year in YEARS:
            # Creem les gràfiques
            trips = df.loc[df['year'] == year]
            trips = trips[column].value_counts().sort_index()
            axes[i].plot(trips.index, trips.values, linestyle='--', marker='o', label=f'{year}')
        axes[i].legend()
        axes[i].set_xlabel(xlabel)
        axes[i].set_ylabel(ylabel)
        axes[i].set_title(f"{title} ({column})")

    plt.tight_layout()
    plt.show()


def visualize_histograms(df, column, title, xlabel, ylabel, xlim):
    """
    Funció que crea un histograma a partir de la informació que conté la columna del dataframe

    Parameters:
    - df (DataFrame): El DataFrame amb el que treballem.
    - column (str): Columna que volem estudiar.
    - title (str): Títol de la taula.
    - xlabel (str): Nom de l'eix horitzonal.
    - ylabel (str): Nom de l'eix vertcial.
    - xlim (range): Límit que tindran els nostres valors.
    """

    # Creem el gràfic
    plt.figure(figsize=(15,6))

    # Comptem els viatges per cada any
    for year in YEARS:

        data = df[df['year'] == year]
        kwargs = dict(histtype='bar', alpha=0.5, bins=50, label=f'{year}')

        plt.hist(data[column], **kwargs)

    # Configurem el plt
    plt.legend(title="Year")
    plt.xlim(xlim)
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.title(title)
    plt.show()


def analyze_pickup_dropoff_locations(data):
    """
    Funció que analitza dades estadístiques quan PULocationID == DOLocationID

    Parameters:
    - data (DataFrame): El DataFrame amb el que treballem.
    """

    fig, axes = plt.subplots(2, figsize=(15, 10))

    # 1. Obtenim les 5 ubicacions de recollida més freqüents
    # Com que sort_values ordena en ordre ascendent hem d'agafar tail
    trips = data['PULocationID'].value_counts().sort_values().tail(5)

    # Mostrem els viatges on la location ID sigui una de les que hem trobat abans
    pickup_locations = data[data['PULocationID'].isin(trips.keys())]

    # Fem reset del índex per tenir-ho actualitzat
    pickup_locations.reset_index(drop=True, inplace=True)

    print('La taula amb les 5 ubicacions de recollida més freqüents són:')
    display(pickup_locations)

    # 2. Calculem el nombre mitjà de passatgers per trajecte
    pu_plot = pickup_locations.groupby('PULocationID')['passenger_count'].mean()

    # Dibuixem i afegim els valors a sobre de cada barra
    bars = axes[0].bar(pu_plot.index.astype(str), pu_plot.values, color='skyblue', alpha=0.7)
    for bar in bars:
        height = bar.get_height()
        axes[0].text(bar.get_x() + bar.get_width() / 2, height + 0.01, f'{height:.4f}', ha='center', va='bottom', fontsize=10)

    axes[0].set_ylim((0, 1.6))
    axes[0].set_xlabel("Ubicacions més freqüents")
    axes[0].set_ylabel("Mitjana de passatgers")
    axes[0].set_title("Mean passenger_count per ubicació més freqüent")

    # 3. Busquem els trajectes tals que PU == DO
    equal_pudo_table = data[data['PULocationID'] == data['DOLocationID']].reset_index(drop=True)
    pudo_plot = equal_pudo_table['PULocationID'].value_counts().sort_index()

    # Dibuixem i afegim els valors a sobre de cada barra
    pudo_bars = axes[1].bar(pudo_plot.index.astype(str), pudo_plot.values, color='skyblue', alpha=0.7)
    # Mostrem només els xticks per les ubicacions amb valors per sobre del llindar
    visible_ticks = [str(tick) if pudo_plot[tick] > 2000 else '' for tick in pudo_plot.index]
    axes[1].set_xticks(range(len(pudo_plot)))  # Assegurem que els ticks es configuren correctament
    axes[1].set_xticklabels(visible_ticks, rotation=90, ha='center', fontsize=5)

    axes[1].set_ylim((0, 3000))
    axes[1].set_xlabel("Ubicacions totals")
    axes[1].set_ylabel("Quantitat de viatges PU==DO")
    axes[1].set_title("Quantitat de viatges PU==DO per ubicació")

    print('\nLa taula tal que PU==DO és:')
    display(equal_pudo_table)

    equal_pudo_table_rows, equal_pudo_table_columns = equal_pudo_table.shape
    data_rows, data_columns = data.shape

    # Calculem el quocient demanat
    percentatge_de_trajectes = (equal_pudo_table_rows / data_rows) * 100
    print(f'La relació entre els viatges amb PU=DO és: {percentatge_de_trajectes}%')

    # Configurem el fig
    fig.tight_layout()
    fig.show()