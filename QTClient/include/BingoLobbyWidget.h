/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#ifndef _BingoLobbyWidget_h_
#define _BingoLobbyWidget_h_

#include "BingoWidget.h"
#include "ui_LobbyWidget.h"
#include <QTimer>

namespace Bingo {

	typedef struct {
		QString id;
		QString creationTime;
		QString name;
		QList<QVariant> participants;
	} GameInfo;
	/**
	 * @brief The lobby where the user can choose a game.
	 * @note This Widget requires the "Network" class to be already instantiated,
	 * i.e. a server should already be selectee.
	 * @see BingoConnectionWidget
	 */
	class BingoLobbyWidget : public BingoWidget {
		Q_OBJECT

	public:
		BingoLobbyWidget(BingoMainWindow* parentWindow, QWidget* parent);

		// Implements BingoWidget
		void receiveJSON(JSONRequestType type, const QVariant& data);
		void activate();
		void deactivate();

	protected:
		void show();

	private slots:
		void createNewGame();
		void refreshList();
		void viewGameInfo();
		void joinGame();
		void disconnect();
		void gListTimerTimeout() { refreshList(); }

	private:
		Ui::LobbyWidget ui;
		QMap<QString, GameInfo > gameInformation;
		QString lastGameName;

		QTimer* gameListUpdateTimer;
	};
}


#endif