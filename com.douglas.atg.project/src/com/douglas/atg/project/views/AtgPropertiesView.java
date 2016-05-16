/*
 *
 *
 * Copyright (c) 2014, Cube.
 */
package com.douglas.atg.project.views;

import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * @author SIX Douglas (CGI) - z22dsix
 *
 */
public class AtgPropertiesView extends ViewPart {
    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "com.douglas.atg.project.views.AtgPropertiesView";
    private TableViewer viewer;
    private AtgPropertiesViewSorter comparator;

    @Override
    public void createPartControl(final Composite parent) {
        createViewer(parent);
        // Set the sorter for the table
        comparator = new AtgPropertiesViewSorter();
        viewer.setComparator(comparator);
    }

    private void createViewer(final Composite parent) {

        // Table creation
        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        createColumns(parent, viewer);
        final Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        // Content Provider
        viewer.setContentProvider(new ArrayContentProvider());

        // make the selection available to other views
        getSite().setSelectionProvider(viewer);

        // set the sorter for the table

        // define layout for the viewer
        final GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        viewer.getControl().setLayoutData(gridData);
    }

    public TableViewer getViewer() {
        return viewer;
    }

    // create the columns for the table
    private void createColumns(final Composite parent, final TableViewer viewer) {
        final String[] titles = {"Name", "Value" };
        final int[] bounds = {100, 100 };

        // first column is for the first name
        TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(final Object element) {
                @SuppressWarnings("unchecked")
                final Entry<Object, Object> p = (Entry<Object, Object>) element;
                return (String) p.getKey();
            }
        });

        // second column is for the last name
        col = createTableViewerColumn(titles[1], bounds[1], 1);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(final Object element) {
                @SuppressWarnings("unchecked")
                final Entry<Object, Object> p = (Entry<Object, Object>) element;
                return (String) p.getValue();
            }
        });
    }

    private TableViewerColumn createTableViewerColumn(final String title, final int bound, final int colNumber) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(false);
        column.addSelectionListener(getSelectionAdapter(column, colNumber));
        return viewerColumn;
    }

    private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
        final SelectionAdapter selectionAdapter = new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                comparator.setColumn(index);
                final int dir = comparator.getDirection();
                System.out.println("index: " + index + ", column: " + column + ", dir: " + dir);
                viewer.getTable().setSortDirection(dir);
                viewer.getTable().setSortColumn(column);
                viewer.refresh();
            }
        };
        return selectionAdapter;
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * @param props
     */
    public void load(final Properties props) {
        viewer.setInput(props.entrySet());
    }
}
