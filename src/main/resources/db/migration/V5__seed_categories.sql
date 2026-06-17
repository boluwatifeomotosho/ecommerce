-- ─────────────────────────────────────────────────────────────────────────────
-- V5  Seed product categories (modelled after Jumia Nigeria)
-- Root categories first, subcategories second (using slug-based parent lookup)
-- ─────────────────────────────────────────────────────────────────────────────

-- Root categories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order) VALUES
    ('Phones & Tablets',          'phones-tablets',            'Smartphones, tablets, and phone accessories',          NULL, true,  1),
    ('Electronics',               'electronics',               'TVs, audio, cameras, and home appliances',             NULL, true,  2),
    ('Computing',                 'computing',                 'Laptops, desktops, and computer accessories',          NULL, true,  3),
    ('Fashion',                   'fashion',                   'Clothing, shoes, bags, and accessories',               NULL, true,  4),
    ('Home & Kitchen',            'home-kitchen',              'Furniture, kitchen appliances, and home décor',        NULL, true,  5),
    ('Supermarket',               'supermarket',               'Food, beverages, and everyday groceries',              NULL, true,  6),
    ('Health & Beauty',           'health-beauty',             'Skincare, haircare, fragrances, and supplements',      NULL, true,  7),
    ('Baby Products',             'baby-products',             'Baby clothing, food, toys, and nursery essentials',    NULL, true,  8),
    ('Sporting Goods',            'sporting-goods',            'Fitness equipment, team sports, and outdoor gear',     NULL, true,  9),
    ('Automobiles & Motorcycles', 'automobiles-motorcycles',   'Car accessories, electronics, and motorcycle parts',   NULL, true, 10),
    ('Books & Stationery',        'books-stationery',          'Books, office supplies, and art materials',            NULL, true, 11),
    ('Gaming',                    'gaming',                    'Consoles, video games, and gaming accessories',        NULL, true, 12);

-- Phones & Tablets subcategories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order)
SELECT s.name, s.slug, s.description, c.id, true, s.sort_order
FROM categories c
JOIN (VALUES
    ('Smartphones',          'smartphones',           'Android and iOS phones from all brands',     1),
    ('Tablets & iPads',      'tablets-ipads',         'Tablets for work and entertainment',         2),
    ('Phone Accessories',    'phone-accessories',     'Cases, screen protectors, and holders',      3),
    ('Power Banks & Cables', 'power-banks-cables',    'Chargers, power banks, and USB cables',      4)
) AS s(name, slug, description, sort_order) ON c.slug = 'phones-tablets';

-- Electronics subcategories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order)
SELECT s.name, s.slug, s.description, c.id, true, s.sort_order
FROM categories c
JOIN (VALUES
    ('TVs & Video',              'tvs-video',              'Smart TVs, projectors, and streaming devices',    1),
    ('Audio & Sound',            'audio-sound',            'Speakers, headphones, and earphones',             2),
    ('Cameras & Photography',    'cameras-photography',    'DSLR, action cameras, and accessories',           3),
    ('Home Appliances',          'home-appliances',        'Fridges, washing machines, and generators',       4),
    ('Personal Care Electronics','personal-care-electronics','Shavers, hair dryers, and electric massagers', 5)
) AS s(name, slug, description, sort_order) ON c.slug = 'electronics';

-- Computing subcategories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order)
SELECT s.name, s.slug, s.description, c.id, true, s.sort_order
FROM categories c
JOIN (VALUES
    ('Laptops',              'laptops',              'Windows, macOS, and Chromebook laptops',          1),
    ('Desktops & Monitors',  'desktops-monitors',    'Desktop PCs, all-in-ones, and monitors',         2),
    ('Computer Accessories', 'computer-accessories', 'Keyboards, mice, webcams, and hubs',             3),
    ('Storage Devices',      'storage-devices',      'External hard drives, SSDs, and flash drives',   4),
    ('Networking',           'networking',           'Routers, switches, and network accessories',      5)
) AS s(name, slug, description, sort_order) ON c.slug = 'computing';

-- Fashion subcategories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order)
SELECT s.name, s.slug, s.description, c.id, true, s.sort_order
FROM categories c
JOIN (VALUES
    ('Men''s Clothing',   'mens-clothing',    'Shirts, trousers, suits, and traditional wear',  1),
    ('Women''s Clothing', 'womens-clothing',  'Dresses, blouses, skirts, and Ankara styles',    2),
    ('Men''s Shoes',      'mens-shoes',       'Sneakers, loafers, and formal shoes for men',    3),
    ('Women''s Shoes',    'womens-shoes',     'Heels, sandals, and sneakers for women',         4),
    ('Bags & Luggage',    'bags-luggage',     'Handbags, backpacks, and travel luggage',        5),
    ('Jewellery & Watches','jewellery-watches','Necklaces, bracelets, rings, and wristwatches', 6),
    ('Kids'' Fashion',    'kids-fashion',     'Clothing and footwear for children',             7)
) AS s(name, slug, description, sort_order) ON c.slug = 'fashion';

-- Home & Kitchen subcategories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order)
SELECT s.name, s.slug, s.description, c.id, true, s.sort_order
FROM categories c
JOIN (VALUES
    ('Furniture',           'furniture',            'Sofas, beds, wardrobes, and dining sets',     1),
    ('Kitchen Appliances',  'kitchen-appliances',   'Blenders, microwaves, and air fryers',        2),
    ('Bedding & Bath',      'bedding-bath',         'Bed sheets, pillows, towels, and curtains',   3),
    ('Home Décor',          'home-decor',           'Rugs, clocks, wall art, and vases',           4),
    ('Cleaning Supplies',   'cleaning-supplies',    'Detergents, mops, and disinfectants',         5)
) AS s(name, slug, description, sort_order) ON c.slug = 'home-kitchen';

-- Supermarket subcategories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order)
SELECT s.name, s.slug, s.description, c.id, true, s.sort_order
FROM categories c
JOIN (VALUES
    ('Food Staples',         'food-staples',          'Rice, beans, garri, yam flour, and pasta',   1),
    ('Beverages',            'beverages',             'Soft drinks, juices, water, and malt',       2),
    ('Snacks & Confectionery','snacks-confectionery', 'Biscuits, chocolates, chips, and candy',     3),
    ('Cooking Essentials',   'cooking-essentials',    'Oils, spices, tomato paste, and seasonings', 4),
    ('Household Consumables','household-consumables', 'Tissue, toiletries, and cleaning products',  5)
) AS s(name, slug, description, sort_order) ON c.slug = 'supermarket';

-- Health & Beauty subcategories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order)
SELECT s.name, s.slug, s.description, c.id, true, s.sort_order
FROM categories c
JOIN (VALUES
    ('Skincare',              'skincare',              'Moisturisers, sunscreen, toners, and serums', 1),
    ('Haircare',              'haircare',              'Shampoos, conditioners, and hair treatments', 2),
    ('Fragrances & Perfumes', 'fragrances-perfumes',   'Perfumes, body sprays, and deodorants',       3),
    ('Vitamins & Supplements','vitamins-supplements',  'Multivitamins, protein, and herbal supplements',4),
    ('Medical Supplies',      'medical-supplies',      'First aid kits, thermometers, and glucometers',5)
) AS s(name, slug, description, sort_order) ON c.slug = 'health-beauty';

-- Baby Products subcategories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order)
SELECT s.name, s.slug, s.description, c.id, true, s.sort_order
FROM categories c
JOIN (VALUES
    ('Baby Clothing',     'baby-clothing',      'Onesies, rompers, and baby essentials',          1),
    ('Baby Food & Feeding','baby-food-feeding', 'Baby formula, purees, and feeding accessories',  2),
    ('Toys & Games',      'toys-games',         'Educational toys and games for children',        3),
    ('Diapers & Wipes',   'diapers-wipes',      'Disposable and reusable diapers and wipes',      4),
    ('Baby Furniture',    'baby-furniture',     'Cribs, changing tables, and high chairs',        5)
) AS s(name, slug, description, sort_order) ON c.slug = 'baby-products';

-- Sporting Goods subcategories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order)
SELECT s.name, s.slug, s.description, c.id, true, s.sort_order
FROM categories c
JOIN (VALUES
    ('Fitness Equipment',  'fitness-equipment',  'Dumbbells, treadmills, and gym accessories',   1),
    ('Football & Team Sports','football-team-sports','Boots, jerseys, and balls',                2),
    ('Outdoor & Camping',  'outdoor-camping',    'Tents, torches, and adventure gear',           3),
    ('Sports Clothing',    'sports-clothing',    'Trainers, shorts, and performance wear',       4)
) AS s(name, slug, description, sort_order) ON c.slug = 'sporting-goods';

-- Automobiles & Motorcycles subcategories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order)
SELECT s.name, s.slug, s.description, c.id, true, s.sort_order
FROM categories c
JOIN (VALUES
    ('Car Accessories',    'car-accessories',    'Seat covers, mats, and car care products',    1),
    ('Car Electronics',    'car-electronics',    'Dash cams, car stereos, and GPS devices',     2),
    ('Motorcycle Parts',   'motorcycle-parts',   'Chains, helmets, and motorcycle accessories', 3),
    ('Tyres & Batteries',  'tyres-batteries',    'Car tyres, rims, and batteries',              4)
) AS s(name, slug, description, sort_order) ON c.slug = 'automobiles-motorcycles';

-- Books & Stationery subcategories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order)
SELECT s.name, s.slug, s.description, c.id, true, s.sort_order
FROM categories c
JOIN (VALUES
    ('Books & Novels',    'books-novels',     'Fiction, non-fiction, and educational books',  1),
    ('Office Supplies',   'office-supplies',  'Pens, notepads, files, and staplers',          2),
    ('Art & Craft',       'art-craft',        'Paints, canvases, brushes, and craft kits',    3)
) AS s(name, slug, description, sort_order) ON c.slug = 'books-stationery';

-- Gaming subcategories
INSERT INTO categories (name, slug, description, parent_id, active, sort_order)
SELECT s.name, s.slug, s.description, c.id, true, s.sort_order
FROM categories c
JOIN (VALUES
    ('Game Consoles',       'game-consoles',       'PlayStation, Xbox, Nintendo Switch',          1),
    ('Video Games',         'video-games',          'Console and PC game titles',                  2),
    ('Gaming Accessories',  'gaming-accessories',   'Controllers, headsets, and gaming chairs',    3)
) AS s(name, slug, description, sort_order) ON c.slug = 'gaming';
