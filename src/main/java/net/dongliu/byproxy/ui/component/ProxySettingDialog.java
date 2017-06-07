/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package net.dongliu.byproxy.ui.component;

import net.dongliu.byproxy.setting.ProxySetting;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import net.dongliu.commons.Strings;
import net.dongliu.commons.exception.Throwables;

import java.io.IOException;

/**
 * Show second proxy setting.
 */
public class ProxySettingDialog extends MyDialog<ProxySetting> {

    @FXML
    private CheckBox useProxy;
    @FXML
    private RadioButton socks5Radio;
    @FXML
    private RadioButton httpRadio;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField userField;
    @FXML
    private TextField hostField;
    @FXML
    private TextField portFiled;
    @FXML
    private ToggleGroup proxyTypeGroup;

    private final ObjectProperty<ProxySetting> proxySetting = new SimpleObjectProperty<>();

    public ProxySettingDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/proxy_setting.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw Throwables.throwAny(e);
        }

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        setResultConverter((dialogButton) -> {
            ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonData.OK_DONE ? getModel() : null;
        });

        proxySetting.addListener((o, old, n) -> setModel(n));

    }

    @FXML
    void initialize() {
        enable(false);
        useProxy.setSelected(false);
        useProxy.selectedProperty().addListener((b, o, n) -> enable(n));
    }

    private void enable(Boolean n) {
        hostField.setDisable(!n);
        portFiled.setDisable(!n);
        userField.setDisable(!n);
        passwordField.setDisable(!n);
        for (Toggle toggle : proxyTypeGroup.getToggles()) {
            RadioButton radioButton = (RadioButton) toggle;
            radioButton.setDisable(!n);
        }
    }

    public void setModel(ProxySetting proxySetting) {
        useProxy.setSelected(proxySetting.isUse());
        hostField.setText(proxySetting.getHost());
        portFiled.setText(String.valueOf(proxySetting.getPort()));
        userField.setText(proxySetting.getUser());
        passwordField.setText(proxySetting.getPassword());
//        proxyTypeGroup.selectToggle();
        String type = proxySetting.getType();
        if (type.equals("socks5") || type.isEmpty()) {
            socks5Radio.setSelected(true);
        } else if (type.equals("http")) {
            httpRadio.setSelected(true);
        } else {
            throw new RuntimeException("unknown proxy type: " + type);
        }
    }

    public ProxySetting getModel() {
        boolean use = useProxy.isSelected();
        String host = hostField.getText();
        int port = Strings.toInt(portFiled.getText());
        String user = userField.getText();
        String password = passwordField.getText();
        RadioButton radioButton = (RadioButton) proxyTypeGroup.getSelectedToggle();
        String type = (String) radioButton.getUserData();
        return new ProxySetting(type, host, port, user, password, use);
    }

    public ProxySetting getProxySetting() {
        return proxySetting.get();
    }

    public ObjectProperty<ProxySetting> proxySettingProperty() {
        return proxySetting;
    }
}