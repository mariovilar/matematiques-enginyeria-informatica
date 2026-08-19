import os
import json
import re
import pdfplumber

# --- CONFIGURATION ---
ROOT_DIR = "."  # Current directory
OUTPUT_FILE = "metadata_mapping.json"

# --- REGEX PATTERNS ---
# Matches dates like: "8 de juny de 2025"
REGEX_DATE_CATALAN = r"(\d{1,2})\s+(?:de|d')\s*([a-zA-Zç]+)\s+(?:de\s+)?(\d{4})"
# Matches years like: "Curs 2024-2025" or just "2024-2025"
REGEX_YEAR = r"(?:Curs\s+)?(20\d{2}[-/]20\d{2})"
# Matches fields: "Grau en X"
REGEX_FIELD = r"Grau en\s+([A-Z][a-zA-Z\s]+)"

def normalize_date(date_str):
    """Converts Catalan dates to YYYY-MM-DD."""
    months = {
        "gener": "01", "febrer": "02", "març": "03", "abril": "04", "maig": "05", "juny": "06",
        "juliol": "07", "agost": "08", "setembre": "09", "octubre": "10", "novembre": "11", "desembre": "12"
    }
    match = re.search(REGEX_DATE_CATALAN, date_str, re.IGNORECASE)
    if match:
        day, month_text, year = match.groups()
        month_num = months.get(month_text.lower(), "01")
        return f"{year}-{month_num}-{day.zfill(2)}"
    return date_str

def clean_text(text):
    """Removes extra whitespace and newlines."""
    if text:
        return " ".join(text.split())
    return None

def extract_metadata_from_pdf(path):
    # Default values
    meta = {
        "title": "Untitled",
        "subject": "Unknown Subject",
        "field": "Mathematics",
        "professor": "Unknown",
        "year": "2024-2025",
        "date": None
    }

    try:
        with pdfplumber.open(path) as pdf:
            if not pdf.pages: return meta
            
            # Extract first page text
            text = pdf.pages[0].extract_text()
            if not text: return meta
            
            lines = [l.strip() for l in text.split('\n') if l.strip()]

            # --- STRATEGY A: STRUCTURED LABELS (e.g., "Professor: X") ---
            for i, line in enumerate(lines):
                # Professor
                if re.match(r"(?:Professor|Docent)[:\s]+", line, re.IGNORECASE):
                    val = re.sub(r"(?:Professor|Docent)[:\s]+", "", line, flags=re.IGNORECASE).strip()
                    if not val and i + 1 < len(lines): # Check next line if empty
                        val = lines[i+1]
                    if val: meta["professor"] = clean_text(val)

                # Field (Grau)
                field_match = re.search(REGEX_FIELD, line, re.IGNORECASE)
                if field_match:
                    meta["field"] = clean_text(field_match.group(1))

                # Year (Curs)
                year_match = re.search(REGEX_YEAR, line)
                if year_match:
                    meta["year"] = year_match.group(1)

            # --- STRATEGY B: PATTERN MATCHING (Fallback) ---
            
            # Title / Subject Inference
            # Look for big capitalized lines
            ignore_caps = ["UNIVERSITAT DE BARCELONA", "FACULTAT DE", "GRAU EN", "INDEX", "APUNTS", "EXERCICIS"]
            candidates = [l for l in lines if l.isupper() and len(l) > 6 and not any(ig in l for ig in ignore_caps)]
            
            if candidates:
                main_title = clean_text(candidates[0].title())
                meta["subject"] = main_title
                meta["title"] = main_title
            
            # Detect document type to refine title
            header_snippet = "\n".join(lines[:6]).lower()
            if "exercicis" in header_snippet:
                meta["title"] = f"Exercicis: {meta['subject']}"
            elif "apunts" in header_snippet:
                 meta["title"] = f"Apunts: {meta['subject']}"

            # Date Extraction
            date_match = re.search(REGEX_DATE_CATALAN, text, re.IGNORECASE)
            if date_match:
                raw_date = date_match.group(0)
                meta["date"] = normalize_date(raw_date)
                
                # Infer Academic Year if missing
                if meta["year"] == "2024-2025": 
                    y = int(date_match.group(3))
                    m_text = date_match.group(2).lower()
                    # If month is Sept-Dec -> Start of year. Jan-July -> End of year.
                    if m_text in ['setembre', 'octubre', 'novembre', 'desembre']:
                        meta["year"] = f"{y}-{y+1}"
                    else:
                        meta["year"] = f"{y-1}-{y}"

    except Exception as e:
        print(f"⚠️ Error reading {os.path.basename(path)}: {e}")

    return meta

def main():
    results = []
    print("🔍 Scanning PDF files only...")

    for root, dirs, files in os.walk(ROOT_DIR):
        if ".git" in root or "venv" in root: continue # Skip system folders

        for file in files:
            if file.lower().endswith(".pdf"):
                full_path = os.path.join(root, file)
                
                # Get relative path for R2 key
                r2_key = os.path.relpath(full_path, ROOT_DIR).replace("\\", "/")
                
                print(f"Processing: {r2_key}")
                metadata = extract_metadata_from_pdf(full_path)
                
                results.append({
                    "key": r2_key,
                    "meta": metadata
                })

    # Save to JSON
    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(results, f, indent=2, ensure_ascii=False)
    
    print(f"\n✅ Generated metadata for {len(results)} PDFs in '{OUTPUT_FILE}'")

if __name__ == "__main__":
    main()