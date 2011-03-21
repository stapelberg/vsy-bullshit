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

namespace Bingo {
	/**
	 * @brief The lobby where the user can choose a game.
	 * @note This Widget requires the "Network" class to be already instantiated,
	 * i.e. a server should already be selectee.
	 * @see BingoConnectionWidget
	 */
	class BingoGameWidget : public BingoWidget {
		Q_OBJECT

	public:
		BingoGameWidget(BingoMainWindow* parentWindow, QWidget* parent);

		// Implements BingoWidget
		void receiveJSON(JSONRequestType type, const QVariant& data);
		void activate();
		void deactivate();

	private:
		Ui::GameWidget ui;
	};
}
#endif