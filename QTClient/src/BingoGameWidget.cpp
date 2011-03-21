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
		: BingoWidget(parentWindow, parent) {
			ui.setupUi(this);
	}

	// -------------------------------------------------------------------------
	void BingoGameWidget::receiveJSON(JSONRequestType type, const QVariant& data) {
	
	}
	
	// -------------------------------------------------------------------------
	void BingoGameWidget::activate() {
		ui.wordList->clear();
		foreach(QString word, bingoMain->getCurrentGame().words) {
			ui.wordList->addItem(word);
		}

		ui.currentGameName->setText(bingoMain->getCurrentGame().id);
	}
	
	// -------------------------------------------------------------------------
	void BingoGameWidget::deactivate() {
	
	}
}