package com.valdisdot.util.ui.gui.element;

import com.valdisdot.util.data.DataCell;

import javax.swing.*;
import javax.swing.text.JTextComponent;

//for JTextArea, JTextField
public class TextInputElement extends JElement<String> {
    public TextInputElement(String name, JTextComponent textField) {
        textField.setName(name);
        component = textField;
        dataCell = new DataCell<>(textField::getText, textField::setText);
    }

    public TextInputElement(String name) {
        this(name, new JTextField());
    }
}
