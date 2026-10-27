-- Demo staff password: StaffDemo1! (BCrypt). Local stand only.
INSERT INTO users (email, password_hash, role)
VALUES (
  'staff@carshop.local',
  '$2b$10$/pOLdA4wJ3zF25yEgLXR.O9bnLPWnxe.v1D0aHzL1.fkVN8GB446.',
  'STAFF'
);

INSERT INTO car_models (code, name, description, base_price, image_url) VALUES
  ('SEDAN-AURA', 'Aura Sedan', 'Compact city sedan with efficient hybrid drivetrain.', 24990.00, '/images/cars/aura-sedan.svg');

INSERT INTO car_models (code, name, description, base_price, image_url) VALUES
  ('SUV-RIDGE', 'Ridge SUV', 'Family SUV with elevated seating and cargo space.', 32990.00, '/images/cars/ridge-suv.svg');

INSERT INTO car_models (code, name, description, base_price, image_url) VALUES
  ('HATCH-PULSE', 'Pulse Hatch', 'Agile hatchback for daily commuting.', 19990.00, '/images/cars/pulse-hatch.svg');

INSERT INTO trims (car_model_id, code, name, price_delta)
SELECT id, 'BASE', 'Base', 0 FROM car_models WHERE code = 'SEDAN-AURA';

INSERT INTO trims (car_model_id, code, name, price_delta)
SELECT id, 'COMFORT', 'Comfort', 2500 FROM car_models WHERE code = 'SEDAN-AURA';

INSERT INTO trims (car_model_id, code, name, price_delta)
SELECT id, 'BASE', 'Base', 0 FROM car_models WHERE code = 'SUV-RIDGE';

INSERT INTO trims (car_model_id, code, name, price_delta)
SELECT id, 'TRAIL', 'Trail', 4200 FROM car_models WHERE code = 'SUV-RIDGE';

INSERT INTO trims (car_model_id, code, name, price_delta)
SELECT id, 'BASE', 'Base', 0 FROM car_models WHERE code = 'HATCH-PULSE';

INSERT INTO trims (car_model_id, code, name, price_delta)
SELECT id, 'SPORT', 'Sport', 1800 FROM car_models WHERE code = 'HATCH-PULSE';

INSERT INTO colors (code, name, hex_code, price_delta) VALUES ('WHITE', 'Arctic White', '#F8FAFC', 0);
INSERT INTO colors (code, name, hex_code, price_delta) VALUES ('BLACK', 'Graphite Black', '#0F172A', 0);
INSERT INTO colors (code, name, hex_code, price_delta) VALUES ('BLUE', 'Coastal Blue', '#0369A1', 450);
INSERT INTO colors (code, name, hex_code, price_delta) VALUES ('RED', 'Signal Red', '#B91C1C', 650);

INSERT INTO extra_services (code, name, price) VALUES ('EXTENDED_WARRANTY', 'Extended warranty 5y', 1200);
INSERT INTO extra_services (code, name, price) VALUES ('WINTER_PACKAGE', 'Winter package', 800);
INSERT INTO extra_services (code, name, price) VALUES ('PREMIUM_AUDIO', 'Premium audio', 650);

INSERT INTO parts (sku, name, description, price, image_url, stock_qty) VALUES
  ('BRK-PAD-01', 'Ceramic brake pads', 'Front axle ceramic pad set.', 129.90, '/images/parts/brake-pads.svg', 40);

INSERT INTO parts (sku, name, description, price, image_url, stock_qty) VALUES
  ('FLT-OIL-01', 'Oil filter', 'OEM-compatible oil filter.', 24.50, '/images/parts/oil-filter.svg', 120);

INSERT INTO parts (sku, name, description, price, image_url, stock_qty) VALUES
  ('WIP-BLD-01', 'Wiper blades', 'All-season wiper blade pair.', 39.00, '/images/parts/wiper-blades.svg', 75);
