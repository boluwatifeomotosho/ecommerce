-- Add icon column to categories (Material Symbols icon name)
ALTER TABLE categories ADD COLUMN IF NOT EXISTS icon VARCHAR(100) NOT NULL DEFAULT 'category';

-- Root categories
UPDATE categories SET icon = 'smartphone'          WHERE slug = 'phones-tablets';
UPDATE categories SET icon = 'tv'                  WHERE slug = 'electronics';
UPDATE categories SET icon = 'laptop'              WHERE slug = 'computing';
UPDATE categories SET icon = 'checkroom'           WHERE slug = 'fashion';
UPDATE categories SET icon = 'home'                WHERE slug = 'home-kitchen';
UPDATE categories SET icon = 'local_grocery_store' WHERE slug = 'supermarket';
UPDATE categories SET icon = 'spa'                 WHERE slug = 'health-beauty';
UPDATE categories SET icon = 'child_care'          WHERE slug = 'baby-products';
UPDATE categories SET icon = 'fitness_center'      WHERE slug = 'sporting-goods';
UPDATE categories SET icon = 'directions_car'      WHERE slug = 'automobiles-motorcycles';
UPDATE categories SET icon = 'menu_book'           WHERE slug = 'books-stationery';
UPDATE categories SET icon = 'sports_esports'      WHERE slug = 'gaming';

-- Phones & Tablets subcategories
UPDATE categories SET icon = 'smartphone'            WHERE slug = 'smartphones';
UPDATE categories SET icon = 'tablet'                WHERE slug = 'tablets-ipads';
UPDATE categories SET icon = 'cable'                 WHERE slug = 'phone-accessories';
UPDATE categories SET icon = 'battery_charging_full' WHERE slug = 'power-banks-cables';

-- Electronics subcategories
UPDATE categories SET icon = 'tv'               WHERE slug = 'tvs-video';
UPDATE categories SET icon = 'headphones'       WHERE slug = 'audio-sound';
UPDATE categories SET icon = 'photo_camera'     WHERE slug = 'cameras-photography';
UPDATE categories SET icon = 'kitchen'          WHERE slug = 'home-appliances';
UPDATE categories SET icon = 'self_improvement' WHERE slug = 'personal-care-electronics';

-- Computing subcategories
UPDATE categories SET icon = 'laptop'    WHERE slug = 'laptops';
UPDATE categories SET icon = 'monitor'   WHERE slug = 'desktops-monitors';
UPDATE categories SET icon = 'keyboard'  WHERE slug = 'computer-accessories';
UPDATE categories SET icon = 'storage'   WHERE slug = 'storage-devices';
UPDATE categories SET icon = 'wifi'      WHERE slug = 'networking';

-- Fashion subcategories
UPDATE categories SET icon = 'man'            WHERE slug = 'mens-clothing';
UPDATE categories SET icon = 'woman'          WHERE slug = 'womens-clothing';
UPDATE categories SET icon = 'hiking'         WHERE slug = 'mens-shoes';
UPDATE categories SET icon = 'directions_walk' WHERE slug = 'womens-shoes';
UPDATE categories SET icon = 'luggage'        WHERE slug = 'bags-luggage';
UPDATE categories SET icon = 'watch'          WHERE slug = 'jewellery-watches';
UPDATE categories SET icon = 'child_care'     WHERE slug = 'kids-fashion';

-- Home & Kitchen subcategories
UPDATE categories SET icon = 'chair'     WHERE slug = 'furniture';
UPDATE categories SET icon = 'blender'   WHERE slug = 'kitchen-appliances';
UPDATE categories SET icon = 'bed'       WHERE slug = 'bedding-bath';
UPDATE categories SET icon = 'palette'   WHERE slug = 'home-decor';
UPDATE categories SET icon = 'cleaning'  WHERE slug = 'cleaning-supplies';

-- Supermarket subcategories
UPDATE categories SET icon = 'rice_bowl'          WHERE slug = 'food-staples';
UPDATE categories SET icon = 'local_drink'        WHERE slug = 'beverages';
UPDATE categories SET icon = 'cookie'             WHERE slug = 'snacks-confectionery';
UPDATE categories SET icon = 'skillet'            WHERE slug = 'cooking-essentials';
UPDATE categories SET icon = 'soap'               WHERE slug = 'household-consumables';

-- Health & Beauty subcategories
UPDATE categories SET icon = 'face'             WHERE slug = 'skincare';
UPDATE categories SET icon = 'dry_cleaning'     WHERE slug = 'haircare';
UPDATE categories SET icon = 'water_drop'       WHERE slug = 'fragrances-perfumes';
UPDATE categories SET icon = 'medication'       WHERE slug = 'vitamins-supplements';
UPDATE categories SET icon = 'medical_services' WHERE slug = 'medical-supplies';

-- Baby Products subcategories
UPDATE categories SET icon = 'checkroom'             WHERE slug = 'baby-clothing';
UPDATE categories SET icon = 'baby_changing_station' WHERE slug = 'baby-food-feeding';
UPDATE categories SET icon = 'toys'                  WHERE slug = 'toys-games';
UPDATE categories SET icon = 'baby_changing_station' WHERE slug = 'diapers-wipes';
UPDATE categories SET icon = 'crib'                  WHERE slug = 'baby-furniture';

-- Sporting Goods subcategories
UPDATE categories SET icon = 'fitness_center'  WHERE slug = 'fitness-equipment';
UPDATE categories SET icon = 'sports_soccer'   WHERE slug = 'football-team-sports';
UPDATE categories SET icon = 'terrain'         WHERE slug = 'outdoor-camping';
UPDATE categories SET icon = 'sports'          WHERE slug = 'sports-clothing';

-- Automobiles & Motorcycles subcategories
UPDATE categories SET icon = 'build'        WHERE slug = 'car-accessories';
UPDATE categories SET icon = 'radio'        WHERE slug = 'car-electronics';
UPDATE categories SET icon = 'two_wheeler'  WHERE slug = 'motorcycle-parts';
UPDATE categories SET icon = 'tire_repair'  WHERE slug = 'tyres-batteries';

-- Books & Stationery subcategories
UPDATE categories SET icon = 'auto_stories' WHERE slug = 'books-novels';
UPDATE categories SET icon = 'edit'         WHERE slug = 'office-supplies';
UPDATE categories SET icon = 'brush'        WHERE slug = 'art-craft';

-- Gaming subcategories
UPDATE categories SET icon = 'sports_esports'  WHERE slug = 'game-consoles';
UPDATE categories SET icon = 'videogame_asset' WHERE slug = 'video-games';
UPDATE categories SET icon = 'headset'         WHERE slug = 'gaming-accessories';
