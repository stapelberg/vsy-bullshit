/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#include "BingoLobbyWidget.h"
#include "BingoMainWindow.h"
#include "JSONRequests.h"

namespace Bingo {

	// -------------------------------------------------------------------------
	BingoLobbyWidget::BingoLobbyWidget(BingoMainWindow* parentWindow, QWidget* parent)
		: BingoWidget(parentWindow, parent) {
			ui.setupUi(this);

			connect(ui.createGame, SIGNAL(clicked()), this, SLOT(createNewGame()));
			connect(ui.refreshButton, SIGNAL(clicked()), this, SLOT(refreshList()));
			connect(ui.currentGamesList, SIGNAL(itemSelectionChanged()), this, SLOT(viewGameInfo()));
			connect(ui.joinGameButton, SIGNAL(clicked()), this, SLOT(joinGame()));
	}

	// -------------------------------------------------------------------------
	void BingoLobbyWidget::show() {
		ui.playerNickLabel->setText("Welcome, " + bingoMain->getNick());
		refreshList();

		QWidget::show();
	}
	// -------------------------------------------------------------------------
	void BingoLobbyWidget::refreshList() {
		// Retrieve current Games
		bingoMain->jsonRequest("CurrentGames");
	}

	// -------------------------------------------------------------------------
	void BingoLobbyWidget::receiveJSON(JSONRequestType type, const QVariant& data) {
		if(type == JSON_Get) {
			ui.currentGamesList->clear();

			foreach(QVariant m, data.toList()) {
				QString gameID = m.toMap()["id"].toString();
				gameInformation[gameID] =
					m.toMap()["participants"].toList();
				ui.currentGamesList->addItem(gameID);
			}

		} else if(type == JSON_CREATE_GAME) {
			QVariantMap gameData = data.toMap();
			QString gameID = gameData["id"].toString();
			QList<QString> words;
			
			foreach(QVariant word, gameData["words"].toList()) {
				words.push_back(word.toString());
			}

			bingoMain->setCurrentGame(gameID, words);
			bingoMain->setActiveWidget(WIDGET_GAME);

		} else if (type == JSON_JOIN_GAME){
			bingoMain->setActiveWidget(WIDGET_GAME);
		}
	}

	// -------------------------------------------------------------------------	
	void BingoLobbyWidget::joinGame() {
		QString gameID = ui.currentGamesList->currentItem()->text();

		// TODO: Get words from current game.
		QList<QString> words;

		bingoMain->setCurrentGame(gameID, words);
		
		JSONJoinGame joinRequest;
		joinRequest.setToken(bingoMain->getToken());
		joinRequest.setID(gameID);

		bingoMain->jsonRequest("JoinGame", &joinRequest);
	}
	
	// -------------------------------------------------------------------------
	void BingoLobbyWidget::activate() {
	   this->refreshList();
	}
	
	// -------------------------------------------------------------------------
	void BingoLobbyWidget::deactivate() {
	
	}

	// -------------------------------------------------------------------------
	void BingoLobbyWidget::viewGameInfo() {
		ui.participants->clear();
		QString currentItem = ui.currentGamesList->currentItem()->text();
		
		foreach(QVariant v, gameInformation[currentItem]) {
			ui.participants->addItem(v.toString());
		}
	}

	// -------------------------------------------------------------------------
	void BingoLobbyWidget::createNewGame() {
		JSONCreateGame request;
		request.setToken(bingoMain->getToken());
		request.setSize("3");

		bingoMain->jsonRequest("CreateGame",&request);
	}
}