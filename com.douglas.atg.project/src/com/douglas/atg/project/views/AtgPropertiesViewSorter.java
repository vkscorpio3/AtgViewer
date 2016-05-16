/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.views;

import java.util.Map.Entry;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class AtgPropertiesViewSorter extends ViewerComparator {

    private int propertyIndex;
    public static final int DESCENDING = 1;
    private int direction = DESCENDING;

    /**
     * Constructor argument values that indicate to sort items by 
     * description, owner or percent complete.
     */
    public final static int KEY = 0;
    public final static int VALUE = 1;

    /**
     * Creates a resource sorter that will use the given sort criteria.
     *
     * @param criteria the sort criterion to use: one of <code>NAME</code> or 
     *   <code>TYPE</code>
     */
    public AtgPropertiesViewSorter() {
        super();
        this.propertyIndex = 0;
        direction = 1 - DESCENDING;
    }

    public int getDirection() {
        return direction == 1 ? SWT.DOWN : SWT.UP;
    }

    public void setColumn(final int column) {
        if (column == this.propertyIndex) {
            // Same column as last sort; toggle the direction
            direction = 1 - direction;
        } else {
            // New column; do an ascending sort
            this.propertyIndex = column;
            direction = DESCENDING;
        }
    }

    /* (non-Javadoc)
     * Method declared on ViewerSorter.
     */
    @Override
    @SuppressWarnings("unchecked")
    public int compare(final Viewer viewer, final Object o1, final Object o2) {

        final Entry<Object, Object> entry1 = (Entry<Object, Object>) o1;
        final Entry<Object, Object> entry2 = (Entry<Object, Object>) o2;

        int rc = 0;

        switch (propertyIndex) {
        case KEY:
            rc = compareKeys(entry1, entry2);
            break;
        case VALUE:
            rc = compareValues(entry1, entry2);
            break;
        default:
            rc = 0;
            break;
        }

        // If descending order, flip the direction
        if (direction == DESCENDING) {
            rc = -rc;
        }

        return rc;
    }

    /**
     * Returns a number reflecting the collation order of the given entries
     * based on the key.
     *
     * @param entry1 the first entry element to be ordered
     * @param entry2 the second entry element to be ordered
     * @return a negative number if the first element is less  than the 
     *  second element; the value <code>0</code> if the first element is
     *  equal to the second element; and a positive number if the first
     *  element is greater than the second element
     */
    private int compareKeys(final Entry<Object, Object> entry1, final Entry<Object, Object> entry2) {
        return ((String) entry1.getKey()).compareTo((String) entry2.getKey());
    }

    /**
     * Returns a number reflecting the collation order of the given entries
     * based on the value.
     *
     * @param entry1 the first entry element to be ordered
     * @param entry2 the second entry element to be ordered
     * @return a negative number if the first element is less  than the 
     *  second element; the value <code>0</code> if the first element is
     *  equal to the second element; and a positive number if the first
     *  element is greater than the second element
     */
    protected int compareValues(final Entry<Object, Object> entry1, final Entry<Object, Object> entry2) {
        return (entry1.getValue().toString()).compareTo(entry2.getValue().toString());
    }
}
