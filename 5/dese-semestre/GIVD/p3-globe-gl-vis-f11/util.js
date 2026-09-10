// Custom shaders: Basic shader
const basicShaders = {
  vertexShader: `
    varying vec3 vNormal;
    varying vec2 vUv;
    void main() {
        vNormal = normalize(normalMatrix * normal);
        vUv = uv;
        gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
    }
  `,
  fragmentShader: `
    uniform sampler2D textureMap;
    varying vec3 vNormal;
    varying vec2 vUv;
    void main() {
        vec4 diffuse = texture2D(textureMap, vUv);
        gl_FragColor = diffuse;
    }
  `
};

/**
 * Convert the population data into points
 * @param {object} data - The data to be formatted
 * @return {Promise<object>} - The data parsed into a dictionary
 */
async function parsePointsData(data) {
  let dataObject = {};

  // Format the data into a dictionary
  data.forEach(item => {
    const values = [];
    for (let i = 0; i < item[1].length; i += 3) {
      const point = {
        lat: item[1][i],
        lng: item[1][i + 1],
        population: item[1][i + 2]
      };
      values.push(point);
    }
    dataObject[item[0]] = values;
  });

  return dataObject;
}

function haversineDistance(coords1, coords2) { // coords són [lng, lat]
  const toRad = x => (x * Math.PI) / 180;
  const R = 6371; // Radi de la Terra en km

  // Comprovació robusta per a valors invàlids o NaN dins de haversineDistance
  if (isNaN(coords1[0]) || isNaN(coords1[1]) || isNaN(coords2[0]) || isNaN(coords2[1])) {
    return 0; // Retorna 0 si les coordenades són invàlides per evitar la propagació de NaN
  }

  const dLat = toRad(coords2[1] - coords1[1]);
  const dLon = toRad(coords2[0] - coords1[0]);
  const lat1 = toRad(coords1[1]);
  const lat2 = toRad(coords2[1]);

  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return Math.abs(Math.round(R * c));
}

/**
 * Convert the submarine data into paths with calculated length and color.
 * @param {object} geoJsonData - The GeoJSON data to be formatted.
 * @return {Promise<Array<object>>} - An array of path objects.
 */
// Fitxer: util.js
async function parsePathsData(geoJsonData) {
  const cablePaths = [];
  geoJsonData.features.forEach(feature => {
    const { geometry, properties: originalProperties } = feature;
    let totalLengthKmForFeature = 0;

    // Primer, calcula la longitud total de la característica (sumant tots els seus segments LineString)
    if (geometry.type === 'MultiLineString') {
      geometry.coordinates.forEach(lineStringCoords => { // lineStringCoords és un array de punts [lng, lat]
        for (let i = 0; i < lineStringCoords.length - 1; i++) {
          // Comprovació per assegurar-se que les coordenades són vàlides abans de calcular la distància
          if (lineStringCoords[i] && lineStringCoords[i+1] &&
              !isNaN(lineStringCoords[i][0]) && !isNaN(lineStringCoords[i][1]) &&
              !isNaN(lineStringCoords[i+1][0]) && !isNaN(lineStringCoords[i+1][1])) {
            totalLengthKmForFeature += haversineDistance(lineStringCoords[i], lineStringCoords[i + 1]);
          } else {
            console.warn('Coordenades invàlides o insuficients en un segment de MultiLineString per a la característica:', originalProperties.name);
          }
        }
      });
    } else if (geometry.type === 'LineString') {
      for (let i = 0; i < geometry.coordinates.length - 1; i++) {
        // Comprovació similar per a LineString
        if (geometry.coordinates[i] && geometry.coordinates[i+1] &&
            !isNaN(geometry.coordinates[i][0]) && !isNaN(geometry.coordinates[i][1]) &&
            !isNaN(geometry.coordinates[i+1][0]) && !isNaN(geometry.coordinates[i+1][1])) {
            totalLengthKmForFeature += haversineDistance(geometry.coordinates[i], geometry.coordinates[i + 1]);
        } else {
            console.warn('Coordenades invàlides o insuficients en un LineString per a la característica:', originalProperties.name);
        }
      }
    }

    // Ara, crea objectes de camí per a globe.gl.
    // Cada objecte de camí representarà un únic segment LineString.
    // Tots els segments de la mateixa característica original compartiran la mateixa totalLengthKmForFeature.
    if (geometry.type === 'MultiLineString') {
      geometry.coordinates.forEach(lineStringCoords => {
        // Comprova si el segment té almenys 2 punts per ser un camí vàlid
        if (lineStringCoords && lineStringCoords.length >= 2) {
            const segmentProperties = { ...originalProperties, length_km: totalLengthKmForFeature };
            cablePaths.push({ coords: lineStringCoords, properties: segmentProperties });
        } else {
            console.warn('S\'està ometent un segment de MultiLineString degenerat (menys de 2 punts) per a la característica:', originalProperties.name);
        }
      });
    } else if (geometry.type === 'LineString') {
      // Comprova si el LineString té almenys 2 punts
      if (geometry.coordinates && geometry.coordinates.length >= 2) {
          const featureProperties = { ...originalProperties, length_km: totalLengthKmForFeature };
          cablePaths.push({ coords: geometry.coordinates, properties: featureProperties });
      } else {
          console.warn('S\'està ometent un LineString degenerat (menys de 2 punts) per a la característica:', originalProperties.name);
      }
    }
  });
  return cablePaths;
}

/**
 * Convert the heatmap data into a dictionary
 * @param {object} data - The data to be formatted
 * @return {Promise<object>} - The data parsed into a dictionary
 */
async function parseHeatmapData(data) {
  return data;
}

/**
 * Read data from a file
 * @param {string} fileLocation - The location of the JSON file
 * @param {function} parseDataCallback - The data parse function
 * @return {Promise<object>} - The parsed data
 */
async function getDataFetch(fileLocation, parseDataCallback) {
  // We're going to ask a file for the JSON data.
  const response = await fetch(fileLocation);
  if (!response.ok) {
      throw new Error('File not found');
  }
  const data = await response.json();
  return await parseDataCallback(data);
}

// Convert a number into a simpler version. 
function formatShort(val) {
  if (val >= 1e9) return (val / 1e9).toFixed(1) + 'B';
  if (val >= 1e6) return (val / 1e6).toFixed(1) + 'M';
  return (val / 1e3).toFixed(1) + 'K';
}

export {
  basicShaders,
  parsePointsData,
  parsePathsData,
  parseHeatmapData,
  getDataFetch,
  formatShort
};