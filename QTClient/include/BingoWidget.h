/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#ifndef _BingoWidget_h_
#define _BingoWidget_h_
#include <QWidget>
#include "BingoMainWindow.h"
#include "JSONRequestTypes.h"

/**
* @brief Base class for Widgets.
*/
namespace Bingo {
	class BingoWidget : public QWidget {
		Q_OBJECT
	public:
		BingoWidget(BingoMainWindow* parentWindow, 
			QWidget* parent = 0, 
			Qt::WindowFlags f = 0) 
			: bingoMain(parentWindow), QWidget(parent, f) {

		}

		/**
		 * Callback function for requested JSON Data. 
		 */
		virtual void receiveJSON(JSONRequestType type, const QVariant& data) = 0;

	protected:
		BingoMainWindow* bingoMain;
	};
}

#endif