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

namespace Bingo {
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

	private:
		Ui::LobbyWidget ui;
		QMap<QString, QList<QVariant> > gameInformation;
		QString lastGameID;
	};
}


#endif