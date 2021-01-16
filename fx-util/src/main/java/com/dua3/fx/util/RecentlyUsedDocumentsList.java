package com.dua3.fx.util;

import com.dua3.utility.data.Pair;
import com.dua3.utility.io.IOUtil;

import java.net.URI;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

/**
 * An implementation of an LRU list for documents with automatic storage/ retrieval via preferences API.
 */
public class RecentlyUsedDocumentsList {
    
    private static final Logger LOG = Logger.getLogger(RecentlyUsedDocumentsList.class.getName());
    
    private final int capacity;
    private final LinkedHashMap<URI, String> items = new LinkedHashMap<>();
    private final Preferences prefs;
    private final List<UpdateListener> listeners = new ArrayList<>();
    
    @FunctionalInterface
    public interface UpdateListener {
        void onUpdate(RecentlyUsedDocumentsList source);
    }
    
    /**
     * Construct new RUD list. 
     * NOTE: The preferences node must be exclusive for this LRU instance because it will be cleared on update.
     * @param  prefs the preferences node
     * @param capacity the list capacity
     */
    public RecentlyUsedDocumentsList(Preferences prefs, int capacity) {
        this.capacity = capacity;
        this.prefs = Objects.requireNonNull(prefs);
        load();
    }

    /**
     * Add update listener.
     * @param listener the update listener
     */
    public void addUpdateListener(UpdateListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    /**
     * Remove update listener.
     * @param listener the listener to remove
     */
    public void removeUpdateListener(UpdateListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Put document into list (if not present) or update it's position in the list (if present).
     * @param uri the document's URI
     * @param name the document's display name
     */
    public void put(URI uri, String name) {
        if (storeItem(uri, name)) {
            shrinkToFit();
            changed();
        }
    }

    /**
     * Store item. This method will not update the backing store.
     * @param uri the item's URI
     * @param name the item's display name; if empty, the path of the URI will be used
     * @return true, if item was added
     */
    private boolean storeItem(URI uri, String name) {
        if (name.isEmpty()) {
            name = uri.getPath();
        }
        
        if (name==null || name.isBlank()) {
            return false;
        }
        
        // remove before put to force update the entry's poisition in the list.
        items.remove(uri);
        items.put(uri, name);
        
        return true;
    }

    /**
     * Store item. This method will not update the backing store.
     * @param uriStr the item's URI as a String
     * @param name the item's display name
     */
    private void storeItem(String uriStr, String name) {
        URI uri = IOUtil.toURI(uriStr);
        storeItem(uri, name);
    }
    
    /**
     * Remove excessive items from list.
     */
    private void shrinkToFit() {
        if (items.size() > capacity) {
            URI[] keys = items.keySet().toArray(URI[]::new);
            for (int i = capacity; i < keys.length; i++) {
                items.remove(keys[i]);
            }
        }
    }

    /**
     * Store current list in preferences.
     */
    private void store() {
        try {
            prefs.clear();
            int idx = 0;
            for (var entry: items.entrySet()) {
                prefs.put(String.valueOf(idx++), entry.getKey()+"\n"+entry.getValue());
            }
            prefs.flush();
        } catch (BackingStoreException e) {
            LOG.log(Level.WARNING, "error storing preferences", e);
        }         
    }

    /**
     * Read items from preferences and store in list.
     */
    private void load() {
        try {
            Arrays.stream(prefs.keys())
                    // ... sortieren
                    .sorted(Comparator.comparing(Integer::valueOf))
                    // ... Werte holen
                    .map(key -> prefs.get(key, ""))
                    // ... leere Einträge ignorieren
                    .filter(not(String::isEmpty))
                    // ... in URI und Name aufteilen
                    .map(s -> s.split("\n", 2))
                    // ... eintragen
                    .forEach(item -> storeItem(item[0], item.length > 1 ? item[1] : ""));

            shrinkToFit();
        } catch (BackingStoreException e) {
            LOG.log(Level.WARNING, "error loading preferences", e);
        }
    }

    /**
     * Clear the list.
     */
    public void clear() {
        items.clear();
        changed();
    }

    /**
     * Get list of items. The returned list is not backed by the recent document list (i.e. changes do not write through).
     * @return the list of stored items
     */
    public List<Pair<URI,String>> entries() {
        return items.entrySet().stream().map(Pair::of).collect(Collectors.toList());
    }

    /**
     * Update preferences and inform listeners about update of list.
     */
    private void changed() {
        store();
        for (var listener: listeners) {
            listener.onUpdate(this);
        }
    }

    @Override
    public String toString() {
        return "RecentlyUsedDocumentsList{" +
               "capacity=" + capacity +
               ", items=" + items +
               '}';
    }
}