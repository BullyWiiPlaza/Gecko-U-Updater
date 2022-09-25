package com.wiiudev.gecko.updater.swing;

import lombok.val;
import lombok.var;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import static javax.swing.JLabel.CENTER;

class JTableUtilities
{
	private static void setCellsAlignment(JTable table)
	{
		val rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(CENTER);

		val tableModel = table.getModel();

		for (var columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++)
		{
			table.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
		}
	}

	private static void setHeaderAlignment(JTable table)
	{
		val header = table.getTableHeader();
		val headerRenderer = new HeaderRenderer(table);
		header.setDefaultRenderer(headerRenderer);
	}

	static void deleteAllRows(JTable table)
	{
		val defaultTableModel = (DefaultTableModel) table.getModel();
		defaultTableModel.setRowCount(0);
	}

	@SuppressWarnings("SameParameterValue")
	static void configureTable(JTable table, String[] columnHeaderNames)
	{
		val tableModel = (DefaultTableModel) table.getModel();
		tableModel.setColumnCount(columnHeaderNames.length);
		tableModel.setColumnIdentifiers(columnHeaderNames);
		setHeaderAlignment(table);

		table.setModel(tableModel);
		val tableHeader = table.getTableHeader();
		tableHeader.setReorderingAllowed(false);
		tableHeader.setResizingAllowed(false);
		tableHeader.setVisible(true);
		setCellsAlignment(table);
	}

	static DefaultTableModel getDefaultTableModel()
	{
		return new DefaultTableModel()
		{
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
	}

	static void setSingleSelection(JTable table)
	{
		table.setSelectionModel(new ForcedListSelectionModel());
	}

	private static class ForcedListSelectionModel extends DefaultListSelectionModel
	{
		ForcedListSelectionModel()
		{
			setSelectionMode(SINGLE_SELECTION);
		}

		@Override
		public void clearSelection()
		{
		}

		@Override
		public void removeSelectionInterval(int start, int end)
		{
		}
	}

	private static class HeaderRenderer implements TableCellRenderer
	{
		private final DefaultTableCellRenderer renderer;

		HeaderRenderer(JTable table)
		{
			renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
			renderer.setHorizontalAlignment(CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		                                               boolean hasFocus, int row, int col)
		{
			return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		}
	}
}
