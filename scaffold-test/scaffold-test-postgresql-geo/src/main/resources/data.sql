INSERT INTO map_place (id, name, location)
VALUES (1, '人民广场', ST_SetSRID(ST_MakePoint(121.4737, 31.2304), 4326)),
       (2, '上海虹桥站', ST_SetSRID(ST_MakePoint(121.3279, 31.2003), 4326)),
       (3, '陆家嘴', ST_SetSRID(ST_MakePoint(121.4998, 31.2397), 4326)),
       (4, '上海南站', ST_SetSRID(ST_MakePoint(121.4300, 31.1546), 4326))
ON CONFLICT (id) DO UPDATE
    SET name     = EXCLUDED.name,
        location = EXCLUDED.location;

SELECT setval('map_place_id_seq', (SELECT max(id) FROM map_place));

INSERT INTO map_region (id, name, boundary)
VALUES (1,
        '上海核心区示例围栏',
        ST_Multi(ST_GeomFromText('POLYGON((
            121.3900 31.1500,
            121.5600 31.1500,
            121.5600 31.2800,
            121.3900 31.2800,
            121.3900 31.1500
        ))', 4326))),
       (2,
        '虹桥示例围栏',
        ST_Multi(ST_GeomFromText('POLYGON((
            121.2800 31.1600,
            121.3800 31.1600,
            121.3800 31.2400,
            121.2800 31.2400,
            121.2800 31.1600
        ))', 4326)))
ON CONFLICT (id) DO UPDATE
    SET name     = EXCLUDED.name,
        boundary = EXCLUDED.boundary;

SELECT setval('map_region_id_seq', (SELECT max(id) FROM map_region));
