package com.justjava.ecommerce.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.function.Predicate;

@Component
public class SlugUtils {

    public String toSlug(String text) {
        String normalised = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");       // strip diacritics
        return normalised
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // keep alphanumeric, spaces, hyphens
                .trim()
                .replaceAll("\\s+", "-")          // spaces → hyphens
                .replaceAll("-{2,}", "-");         // collapse consecutive hyphens
    }

    /**
     * Generates a slug that is guaranteed not to exist according to {@code exists}.
     * Appends "-2", "-3", ... until a free slot is found.
     */
    public String generateUnique(String name, Predicate<String> exists) {
        String base = toSlug(name);
        if (!exists.test(base)) return base;
        int suffix = 2;
        String candidate;
        do {
            candidate = base + "-" + suffix++;
        } while (exists.test(candidate));
        return candidate;
    }
}
