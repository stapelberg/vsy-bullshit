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
	inline QList<QString> createWordList(const QVariantMap& data) {
		QList<QString> words;
			
		foreach(QVariant word, data["words"].toList()) {
			words.push_back(word.toString());
		}

		return words;
	}

	// -------------------------------------------------------------------------
	BingoLobbyWidget::BingoLobbyWidget(BingoMainWindow* parentWindow, QWidget* parent)
		: BingoWidget(parentWindow, parent),
			gameListUpdateTimer(new QTimer(this))
	{
		ui.setupUi(this);
		ui.participants->clear();


		gameListUpdateTimer->setInterval(10000);
		gameListUpdateTimer->stop();

		connect(gameListUpdateTimer,SIGNAL(timeout()),this,SLOT(gListTimerTimeout()));
		connect(ui.createGame, SIGNAL(clicked()), this, SLOT(createNewGame()));
		connect(ui.refreshButton, SIGNAL(clicked()), this, SLOT(refreshList()));
		connect(ui.currentGamesList, SIGNAL(itemSelectionChanged()), this, SLOT(viewGameInfo()));
		connect(ui.joinGameButton, SIGNAL(clicked()), this, SLOT(joinGame()));
		connect(ui.disconnectButton, SIGNAL(clicked()), this, SLOT(disconnect()));
	}

	// -------------------------------------------------------------------------
	void BingoLobbyWidget::show() {
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
				QVariantMap& info = m.toMap(); 
				QString gameName = info["name"].toString();

				if(gameName.isEmpty()) {
					gameName = info["id"].toString();
					gameName.truncate(9);
					gameName.prepend("UnnamedGame_");
				}
				
				gameInformation[gameName].name = gameName;
				gameInformation[gameName].id = info["id"].toString();

				gameInformation[gameName].participants =
					info["participants"].toList();
				ui.currentGamesList->addItem(gameName);
			}

		} else if(type == JSON_CREATE_GAME) {
			QVariantMap gameData = data.toMap();
			QString gameID = gameData["id"].toString();
			QString size = ui.gameSize->currentText();
			size.truncate(1);

			bingoMain->setCurrentGame(gameID, 
				gameData["name"].toString(),
				createWordList(gameData),
				size.toInt());

			bingoMain->setActiveWidget(WIDGET_GAME);

		} else if (type == JSON_JOIN_GAME){
			QVariantMap gameData  = data.toMap();
			bingoMain->setCurrentGame(gameInformation[lastGameName].id, 
				lastGameName, 
				createWordList(gameData),
				gameData["size"].toInt());
			bingoMain->setActiveWidget(WIDGET_GAME);
		}
	}

	// -------------------------------------------------------------------------	
	void BingoLobbyWidget::joinGame() {
		if(ui.currentGamesList->selectedItems().size() > 0) {
			lastGameName = ui.currentGamesList->currentItem()->text();
			JSONJoinGame joinRequest;
			joinRequest.setToken(bingoMain->getToken());
			joinRequest.setID(gameInformation[lastGameName].id);

			bingoMain->jsonRequest("JoinGame", &joinRequest);
		} else {
			bingoMain->reportError(tr("No Game selected."));
		}
	}
		
	// -------------------------------------------------------------------------
	void BingoLobbyWidget::disconnect() {
		bingoMain->setActiveWidget(WIDGET_CONNECTION);
	}
		
	// -------------------------------------------------------------------------
	void BingoLobbyWidget::activate() {
	  this->refreshList();
	  ui.playerNickLabel->setText(tr("Connected as %1").arg(bingoMain->getNick()));
	  this->gameListUpdateTimer->start();
	}
	
	// -------------------------------------------------------------------------
	void BingoLobbyWidget::deactivate() {
		this->gameListUpdateTimer->stop();
	}

	// -------------------------------------------------------------------------
	void BingoLobbyWidget::viewGameInfo() {
		if(!gameInformation.isEmpty() 
			&& ui.currentGamesList->selectedItems().size() > 0) { 
			ui.participants->clear();	
			lastGameName = ui.currentGamesList->currentItem()->text();
			
			foreach(QVariant v, gameInformation[lastGameName].participants) {
				ui.participants->addItem(v.toString());
			}
		}
	}

	// -------------------------------------------------------------------------
	void BingoLobbyWidget::createNewGame() {
		if(ui.gameName->text().isEmpty()) {
			bingoMain->reportError("No Game Name was provided.");
		} else { 
			JSONCreateGame request;
			request.setToken(bingoMain->getToken());
			request.setName(ui.gameName->text());
			QString size = ui.gameSize->currentText();
			size.truncate(1);
			request.setSize(size);

			bingoMain->jsonRequest("CreateGame",&request);
		}
	}
}