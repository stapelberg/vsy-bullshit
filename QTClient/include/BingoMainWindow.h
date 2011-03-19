/*

 */
#ifndef _BingoMainWindow_h_
#define _BingoMainWindow_h_

#include <QtGui/QMainWindow>

// Include uic generated UI header
#include "ui_BingoMainUI.h"

/**
 * @brief The Mainwindow
 *
 */
class BingoMainWindow : public QMainWindow
{
	Q_OBJECT

public:
	BingoMainWindow(QWidget *parent = 0, Qt::WFlags flags = 0);
	~BingoMainWindow();

public slots:

//protected:
//	void closeEvent(QCloseEvent *event);

private:
	Ui::BingoMainUI ui;

};

#endif // _BingoMainWindow_h_
