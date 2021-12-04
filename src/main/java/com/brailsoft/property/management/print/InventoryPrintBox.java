package com.brailsoft.property.management.print;

import com.brailsoft.property.management.model.InventoryItem;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class InventoryPrintBox extends PrintBox {
	private InventoryItem inventoryItem;
	private Label manufacturerLabel;
	private Label modelLabel;
	private Label serialnumberLabel;
	private Label supplierLabel;
	private Label purchasedateLabel;

	public InventoryPrintBox(InventoryItem inventoryItem) {
		super();
		this.inventoryItem = inventoryItem;
		this.setSpacing(5.0);
		createGUI();
	}

	@Override
	protected void bind() {
		manufacturerLabel.textProperty().bind(inventoryItem.manufacturerProperty());
		modelLabel.textProperty().bind(inventoryItem.modelProperty());
		serialnumberLabel.textProperty().bind(inventoryItem.serialNumberProperty());
		supplierLabel.textProperty().bind(inventoryItem.supplierProperty());
		purchasedateLabel.textProperty().bind(inventoryItem.purchaseDateProperty());
	}

	@Override
	protected void makeLabels() {
		makeDescriptionLabel();
		createDivider();
		makeManufacturerLabels();
		createDivider();
		makeModelLabels();
		createDivider();
		makeSerialnumberLabels();
		createDivider();
		makeSupplierLabels();
		createDivider();
		makePurchasedateLabels();
	}

	@Override
	protected void setStyles() {
		setManufacturerStyle();
		setModelStyle();
		setSerialnumberStyle();
		setSupplierStyle();
		setPurchasedateStyle();
	}

	private void makeDescriptionLabel() {
		Label label = new Label(inventoryItem.getDescription());
		label.setFont(new Font(FONT_SIZE));
		getChildren().add(label);
	}

	private void makeManufacturerLabels() {
		VBox vBox = new VBox();
		Label label;
		label = new Label("Manufacturer");
		label.setFont(new Font(FONT_SIZE));
		vBox.getChildren().add(label);
		manufacturerLabel = new Label();
		vBox.getChildren().add(manufacturerLabel);
		getChildren().add(vBox);
	}

	private void makeModelLabels() {
		VBox vBox = new VBox();
		Label label;
		label = new Label("Model");
		label.setFont(new Font(FONT_SIZE));
		vBox.getChildren().add(label);
		modelLabel = new Label();
		vBox.getChildren().add(modelLabel);
		getChildren().add(vBox);
	}

	private void makeSerialnumberLabels() {
		VBox vBox = new VBox();
		Label label;
		label = new Label("Serial Number");
		label.setFont(new Font(FONT_SIZE));
		vBox.getChildren().add(label);
		serialnumberLabel = new Label();
		vBox.getChildren().add(serialnumberLabel);
		getChildren().add(vBox);
	}

	private void makeSupplierLabels() {
		VBox vBox = new VBox();
		Label label;
		label = new Label("Supplier");
		label.setFont(new Font(FONT_SIZE));
		vBox.getChildren().add(label);
		supplierLabel = new Label();
		vBox.getChildren().add(supplierLabel);
		getChildren().add(vBox);
	}

	private void makePurchasedateLabels() {
		VBox vBox = new VBox();
		Label label;
		label = new Label("Date Purchased");
		label.setFont(new Font(FONT_SIZE));
		vBox.getChildren().add(label);
		purchasedateLabel = new Label();
		vBox.getChildren().add(purchasedateLabel);
		getChildren().add(vBox);
	}

	private void setManufacturerStyle() {
		manufacturerLabel.setFont(new Font(FONT_SIZE));
	}

	private void setModelStyle() {
		modelLabel.setFont(new Font(FONT_SIZE));
	}

	private void setSerialnumberStyle() {
		serialnumberLabel.setFont(new Font(FONT_SIZE));
	}

	private void setSupplierStyle() {
		supplierLabel.setFont(new Font(FONT_SIZE));
	}

	private void setPurchasedateStyle() {
		purchasedateLabel.setFont(new Font(FONT_SIZE));
	}
}
