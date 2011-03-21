/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/

#include "BingoGameWidget.h"
#include "BingoMainWindow.h"
#include "JSONRequests.h"

namespace Bingo {

	// -------------------------------------------------------------------------
	BingoGameWidget::BingoGameWidget(BingoMainWindow* parentWindow, QWidget* parent)
		: BingoWidget(parentWindow, parent),
		playerListUpdate(new QTimer(this)) {
			ui.setupUi(this);

			// Update Player list every 5 seconds
			playerListUpdate->setInterval(5000);
			playerListUpdate->stop();
			connect(playerListUpdate,SIGNAL(timeout()),this,SLOT(updatePlayerList()));
	}

	// -------------------------------------------------------------------------
	void BingoGameWidget::receiveJSON(JSONRequestType type, const QVariant& data) {
   		if(type == JSON_Get) {
			ui.playerList->clear();
			QList<QVariant> players;

			foreach(QVariant m, data.toList()) {
				if(m.toMap()["id"].toString().compare(bingoMain->getCurrentGame().id) == 0) { 
					players = m.toMap()["participants"].toList();
				}
			}

			if(!players.isEmpty()) {
				foreach(QVariant p, players) {							 
					ui.playerList->addItem(p.toString());
				}
			}
		}

	}
	
	// -------------------------------------------------------------------------
	void BingoGameWidget::updatePlayerList() {
		bingoMain->jsonRequest("CurrentGames");
	}

	// -------------------------------------------------------------------------
	void BingoGameWidget::activate() {
		playerListUpdate->start();					 
		
		int i = 0;
		int y = 0;
		foreach(QString word, bingoMain->getCurrentGame().words) {
			ui.wordTable->setItem(i,y, new QTableWidgetItem(word));
			i++;
			if(i == ui.wordTable->columnCount()) {
				i = 0;
				y++;
			}
		}

		updatePlayerList();
		ui.gameName->setText(bingoMain->getCurrentGame().id);
	}
	
	// -------------------------------------------------------------------------
	void BingoGameWidget::deactivate() {
	  playerListUpdate->stop();
	
	}
}