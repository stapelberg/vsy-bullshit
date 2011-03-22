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
		playerListUpdate(new QTimer(this)),
		gameTimer(new QTimer(this)) {
			ui.setupUi(this);

			// Update Player list every 5 seconds
			playerListUpdate->setInterval(5000);
			playerListUpdate->stop();

			// Check for Game status every 500 ms
			gameTimer->setInterval(500);
			gameTimer->stop();

			connect(playerListUpdate,SIGNAL(timeout()),this,SLOT(updatePlayerList()));
			connect(gameTimer,SIGNAL(timeout()),this,SLOT(checkWinner()));
			connect(ui.leaveButton, SIGNAL(clicked()), this, SLOT(leave()));
			connect(ui.wordTable, SIGNAL(clicked(const QModelIndex &)), this, SLOT(wordClicked( const QModelIndex &)));
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
		} else if(type == JSON_LEAVE_GAME) {
			bingoMain->setActiveWidget(WIDGET_LOBBY);
		} else if(type == JSON_CHECK_WINNER) {
			if(!data.toMap()["winner"].toString().isEmpty()) {
				gameTimer->stop();
				bingoMain->notify(tr("%1 has won the Game!").arg(data.toMap()["winner"].toString()));
				bingoMain->setActiveWidget(WIDGET_LOBBY);
			}
		} else if(type == JSON_MAKE_MOVE) {
			//..
		}

	}
	
	// -------------------------------------------------------------------------
	void BingoGameWidget::updatePlayerList() {
		bingoMain->jsonRequest("CurrentGames");
	}
	
	// -------------------------------------------------------------------------
	void BingoGameWidget::checkWinner() {
		JSONCheckWinner request;
		request.setID(bingoMain->getCurrentGame().id);
		request.setToken(bingoMain->getToken());

		bingoMain->jsonRequest("CheckWinner", &request);
	}
	
	// -------------------------------------------------------------------------
	void BingoGameWidget::leave() {
		JSONLeaveGame request;
		request.setID(bingoMain->getCurrentGame().id);
		request.setToken(bingoMain->getToken());

	 	bingoMain->jsonRequest("LeaveGame", &request);
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
		ui.gameName->setText(bingoMain->getCurrentGame().name);

		gameTimer->start();
	}
	
	// -------------------------------------------------------------------------
	void BingoGameWidget::deactivate() {
		gameTimer->stop();
	  playerListUpdate->stop();
	
	}

	// -------------------------------------------------------------------------
	void BingoGameWidget::wordClicked(const QModelIndex & index) {
		if(ui.wordTable->item(index.row(), index.column())->flags().testFlag(Qt::ItemIsEnabled)) {
			JSONMakeMove request;
			request.setToken(bingoMain->getToken());
			request.setID(bingoMain->getCurrentGame().id);
			int pos = index.row()*ui.wordTable->columnCount()+index.column();
			request.setField(pos);
			bingoMain->jsonRequest("MakeMove", &request);
		}
		ui.wordTable->item(index.row(), index.column())->setFlags(Qt::NoItemFlags);
		
	}
}