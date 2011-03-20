/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#include "BingoMainWindow.h"

#include <QtGui/QApplication>
#include <QTextCodec>
#include <QSplashScreen>
#include <QPixmap>
#include <QBitmap>

#ifdef _WINDOWS
#include <windows.h>
int __stdcall WinMain (HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpszCmdLine, int nCmdShow) {
		QApplication application(__argc, __argv);
#else
int main(int argc, char *argv[]){
		QApplication application(argc, argv);
#endif
	
	// Set Default Translation Codec to UTF8
	QTextCodec::setCodecForTr(QTextCodec::codecForName("utf8"));

	Bingo::BingoMainWindow mainWindow;
	mainWindow.show();

	return application.exec();
}
