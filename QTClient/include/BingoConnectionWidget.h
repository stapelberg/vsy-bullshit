/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#ifndef _BingoConnectionWidget_h_
#define _BingoConnectionWidget_h_
#include "BingoWidget.h"
#include "ui_ConnectionWidget.h"
#include "JSONRequests.h"

namespace Bingo {
	/**
	 * @brief The first screen, used to establish a connection to a server.
	 *
	 */
	class BingoConnectionWidget : public BingoWidget {
		Q_OBJECT

	public:
		BingoConnectionWidget(QVector<QString> serverList, BingoMainWindow* parentWindow, QWidget* parent);
	
		// Implements BingoWidget
		void receiveJSON(JSONRequestType type, const QVariant& data);

	private slots:
		void connectClient();
	private:
		Ui::ConnectionWidget ui;
		QVector<QString> serverList;
	};
}


#endif