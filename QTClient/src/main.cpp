/*
 *
 */
#include "BingoMainWindow.h"
#include <QtGui/QApplication>
#include <QTextCodec>
#include <QSplashScreen>
#include <QPixmap>
#include <QBitmap>

int main(int argc, char *argv[])
{
	QApplication application(argc, argv);

	// Set Default Translation Codec to UTF8
	QTextCodec::setCodecForTr(QTextCodec::codecForName("utf8"));

	// Display fancy splash screen
	//QPixmap splashImage(":/Headers/Images/splash.png");
	//QSplashScreen* splash= new QSplashScreen(splashImage);
	//splash->setMask(splashImage.mask());
	//splash->show();

	BingoMainWindow mainWindow;
	//splash->finish(&mainWindow);
	mainWindow.show();

	return application.exec();
}
