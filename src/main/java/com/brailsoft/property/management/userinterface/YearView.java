package com.brailsoft.property.management.userinterface;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.brailsoft.property.management.graphic.DateSquare;
import com.brailsoft.property.management.model.PropertyMonitor;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;

public class YearView extends Pane {
	public static final int NUMBER_OF_COLUMNS = 53;

	private static final DateTimeFormatter toolTipFormatter = DateTimeFormatter.ofPattern("EEE dd LLL yyyy");
	private static final int GAP = 4;

	public YearView() {
		super();
		setPrefWidth(NUMBER_OF_COLUMNS * DateSquare.SPACING);
		int xpos = GAP;
		int ypos = GAP;
		LocalDateTime date = LocalDateTime.now();
		for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
			for (int j = 0; j < 7; j++) {
				getChildren().add(createDataSquare(xpos, ypos, date));
				ypos += DateSquare.SPACING;
				date = date.plusDays(1);
			}
			ypos = GAP;
			xpos += DateSquare.SPACING;
		}
	}

	private DateSquare createDataSquare(int xpos, int ypos, LocalDateTime date) {
		int numberOfOverdue = PropertyMonitor.getInstance().getOverdueItemsFor(date).size();
		int numberOfNotified = PropertyMonitor.getInstance().getNotifiedItemsFor(date).size();
		String toolTip = date.format(toolTipFormatter);
		if (numberOfOverdue > 0) {
			toolTip += " - " + numberOfOverdue
					+ (numberOfOverdue == 1 ? " item due for completion" : " items due for completion");
		} else if (numberOfNotified > 0) {
			toolTip += " - " + numberOfNotified
					+ (numberOfNotified == 1 ? " item will be due soon" : " items will be due soon");
		}
		Tooltip t = new Tooltip(toolTip);
		DateSquare rect = new DateSquare(xpos, ypos, numberOfNotified, numberOfOverdue);
		Tooltip.install(rect, t);
		return rect;
	}
}
