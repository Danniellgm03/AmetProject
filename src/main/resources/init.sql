CREATE TABLE IF NOT EXISTS mediciones(
     id INTEGER PRIMARY KEY AUTOINCREMENT,
     localidad TEXT NOT NULL,
     provincia TEXT NOT NULL,
     temp_max REAL NOT NULL,
     hour_temp_max TEXT NOT NULL,
     temp_min REAL NOT NULL,
     hour_temp_min TEXT NOT NULL,
     precipitacion REAL NOT NULL,
     dia TEXT NOT NULL
);