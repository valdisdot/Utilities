package com.valdisdot.util.ui.gui.parser;

import com.valdisdot.util.data.element.DataCellGroup;
import com.valdisdot.util.ui.gui.mold.FrameMold;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

//contact for parsers
public interface MoldParser<P extends JPanel> extends Supplier<P> {
    void parse(FrameMold frameMold);

    DataCellGroup<String> getDataCellGroups();

    Map<String, Consumer<ActionListener>> getButtonsActionListenerConsumers();
}
