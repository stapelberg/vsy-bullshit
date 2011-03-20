/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#include "BingoConnectionWidget.h"
#include "BingoMainWindow.h"
#include "JSONRequests.h"

namespace Bingo {

	// -------------------------------------------------------------------------
	BingoConnectionWidget::BingoConnectionWidget(QVector<QString> serverList,
		BingoMainWindow* parentWindow, QWidget* parent)
		: serverList(serverList), 
		BingoWidget(parentWindow, parent) {
			ui.setupUi(this);

			// Fill list
			foreach(QString serverName, serverList) {
				ui.serverList->addItem(serverName);
			}

			// Connect Actions
			connect(ui.connectButton, SIGNAL(clicked()), this, SLOT(connectClient()));
	}

	// -------------------------------------------------------------------------
	void BingoConnectionWidget::connectClient() {
		// We allow letters and numbers in nicknames.
		QRegExpValidator validator(QRegExp("[a-z|A-Z|0-9]{2,30}"), 0);
		int pos = 0;
		// needed for compatibility with older QT versions.
		QString text = ui.username->text();


		if(!ui.username->text().isEmpty()
			&& validator.validate(text, pos) == QRegExpValidator::Acceptable) {
				// Connect
				bingoMain->setupNetwork(ui.serverList->currentText());

				// Try to login
				JSONRegisterPlayer requestData;
				requestData.setNickname(ui.username->text());

				bingoMain->jsonRequest("RegisterPlayer",  &requestData);
		} else {
			bingoMain->reportError(tr("Username missing or invalid."));
		}
	}

	// -------------------------------------------------------------------------
	void BingoConnectionWidget::receiveJSON(JSONRequestType type, const QVariant& data) {
		if(type == JSON_REGISTER_PLAYER) {
			bingoMain->setNick(ui.username->text());
			bingoMain->setActiveWidget(WIDGET_LOBBY);
			bingoMain->setToken(data.toMap()["token"].toString());
		}
	}

}