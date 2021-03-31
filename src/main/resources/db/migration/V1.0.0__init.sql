DROP TABLE IF EXISTS wines CASCADE;

CREATE TABLE IF NOT EXISTS wines (
    id UUID PRIMARY KEY,
    name TEXT,
    oldPrice DOUBLE,
    newPrice DOUBLE,
    link TEXT,
    image TEXT,
    manufacturer TEXT,
    brand TEXT,
    country TEXT,
    region TEXT[],
    capacity DOUBLE,
    strength DOUBLE,
    color TEXT,
    sugar TEXT,
    grapeSort TEXT[],
    year INTEGER,
    description TEXT,
    gastronomy TEXT,
    taste TEXT,
    flavor TEXT,
    rating DOUBLE,
    sparkling BOOLEAN,
    city TEXT,
    inStock TEXT
);
