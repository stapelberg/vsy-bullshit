/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#ifndef _BingoGameWidget_h_
#define _BingoGameWidget_h_
#include "BingoWidget.h"
#include "ui_GameWidget.h"

#include <QTimer>

namespace Bingo {
	/**
	*
	*/
	class BingoGameWidget : public BingoWidget {
		Q_OBJECT

	public:
		BingoGameWidget(BingoMainWindow* parentWindow, QWidget* parent);

		// Implements BingoWidget
		void receiveJSON(JSONRequestType type, const QVariant& data);
		void activate();
		void deactivate();

	private slots:
		void updatePlayerList();
		void leave();
		void wordClicked(const QModelIndex & index);
		void checkWinner();
	private:
		Ui::GameWidget ui;
		QTimer* playerListUpdate;
		QTimer* gameTimer;
	};
}
#endif