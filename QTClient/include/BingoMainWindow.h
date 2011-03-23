/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#ifndef _BingoMainWindow_h_
#define _BingoMainWindow_h_

#include <QtGui/QMainWindow>
#include "JSONRequestTypes.h"

// Include uic generated UI header
#include "ui_BingoMainUI.h"

// Forward Decl
class JSONRequest;
namespace Bingo {
	class Network;
	class BingoWidget;
}

namespace Bingo {

	// Used to identify widgets
	enum WidgetIdentifiers {
		WIDGET_CONNECTION = 0,
		WIDGET_LOBBY = 1,
		WIDGET_GAME = 2
	};
	
	typedef struct {
		QString id;
		QString name;
		int size;
		QList<QString> words;
	} GameData;
	/**
	* @brief The Main Window, also serving as the main control class, routing all
	* requests and storing global information.
	*/
	class BingoMainWindow : public QMainWindow {
		Q_OBJECT

	public:
		BingoMainWindow(QWidget *parent = 0, Qt::WFlags flags = 0);
		~BingoMainWindow();
		
		/**
		* Report an error to the user. The error will be displayed in the status bar.
		*/
		void reportError(const QString& msg);

		/**
		* Notify the user of an event.
		*/
		void notify(const QString& msg);

		/**
		* Send a JSON request to a remote host, as specified in the Network instance.
		* @note setupNetwork has to be called prior to using this function.
		*/
		void jsonRequest(const QString& command, JSONRequest* requestData = 0);
		
		/**
		* Callback for the Network manager.
		*/
		void jsonResult(const QVariant& data, JSONRequestType type = JSON_Get);
		
		/**
		* Sets up the network for usage.
		*/
		void setupNetwork(const QString& server);

		/**
		* Returns the Size of the Widget container
		*/
		QSize getContainerSize();
			

		// Captain Obvious START
		void setToken(const QString& token);
		const QString& getToken() const;

		void setNick(const QString& nick);
		const QString& getNick() const;
		// Captain Obvious END
				
		/**
		* Set the active widget to be displayed within the main window.
		* @see WidgetIdentifiers
		*/
		void setActiveWidget(int widget);
		void setCurrentGame(const QString& id, 
			const QString& name, QList<QString> words, int size);

		const GameData& getCurrentGame() const;

		Network* getNetwork() {
			return network;
		}
public slots:

	protected:
		void closeEvent(QCloseEvent *event);
		void resizeEvent(QResizeEvent * evt);

	private:
		Ui::BingoMainUI ui;
		QMovie* indicator;
		bool networkActive;
		
		// Global Data
		QString playerToken;
		QString nickname;
		GameData currentGame;

		void setupApplication();
		void loadServerList(QString file);
		
		QVector<QString> servers;
		QVector<BingoWidget*> widgets;
		int activeWidget;

		Network* network;
	};
}
#endif // _BingoMainWindow_h_
