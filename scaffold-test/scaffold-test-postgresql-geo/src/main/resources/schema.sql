CREATE
EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS map_place
(
    id       BIGSERIAL PRIMARY KEY,
    name     TEXT NOT NULL,
    location geometry(Point, 4326
) NOT NULL,
    created_at TIMESTAMPTZ             NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS map_region
(
    id       BIGSERIAL PRIMARY KEY,
    name     TEXT NOT NULL,
    boundary geometry(MultiPolygon, 4326
) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_map_place_location_gist
    ON map_place
    USING GIST (location);

CREATE INDEX IF NOT EXISTS idx_map_place_location_geography_gist
    ON map_place
    USING GIST ((location::geography));

CREATE INDEX IF NOT EXISTS idx_map_region_boundary_gist
    ON map_region
    USING GIST (boundary);
