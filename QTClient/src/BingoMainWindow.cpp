/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#include "BingoMainWindow.h"

#include <QDesktopServices>
#include <QDesktopWidget>
#include <QTextStream>
#include <QFile>
#include <QMessageBox>
#include <QMovie>

#include "Network.h"
#include "JSONRequests.h"

// Custom Widgets
#include "BingoConnectionWidget.h"
#include "BingoLobbyWidget.h"
#include "BingoGameWidget.h"

namespace Bingo {
	// -------------------------------------------------------------------------
	BingoMainWindow::BingoMainWindow(QWidget *parent, Qt::WFlags flags)
		: QMainWindow(parent, flags), 
		network(0),
		indicator(new QMovie(":/Bingo/working.gif")),
		activeWidget(0) {

		ui.setupUi(this);
		setupApplication();
	}
	
	// -------------------------------------------------------------------------
	BingoMainWindow::~BingoMainWindow() {
		delete network;
	}
	// -------------------------------------------------------------------------
	void BingoMainWindow::setupApplication() {
		connect(ui.action_Quit, SIGNAL(triggered()), this, SLOT(menuQuit()));
		connect(ui.actionProject_Website, SIGNAL(triggered()), this, SLOT(menuWebsite()));
		connect(ui.actionDrop_Token, SIGNAL(triggered()), this, SLOT(menuDropToken()));
		connect(ui.actionAbout, SIGNAL(triggered()), this, SLOT(menuAbout()));


		ui.indicator->setMovie(indicator);
		indicator->start();
		indicator->setPaused(true);
		ui.indicator->setVisible(false);

		// Center Window
		QRect frect = frameGeometry();
		frect.moveCenter(QDesktopWidget().availableGeometry().center());
		move(frect.topLeft());

		// Load Server list
		this->loadServerList("servers.list");
		ui.errorMessage->setVisible(false);

		// Prepare Widgets

		// Connection
		widgets.push_back(new BingoConnectionWidget(servers, this, ui.container));
		widgets.at(WIDGET_CONNECTION)->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);

		// Lobby
		widgets.push_back(new BingoLobbyWidget(this, ui.container));
		widgets.at(WIDGET_LOBBY)->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);
		widgets.at(WIDGET_LOBBY)->hide();

		// Game
		widgets.push_back(new BingoGameWidget(this, ui.container));
		widgets.at(WIDGET_GAME)->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);
		widgets.at(WIDGET_GAME)->hide();

		// Make the connect screen visible
		ui.containerLayout->addWidget(widgets.at(activeWidget), 1, 1);
		this->setActiveWidget(WIDGET_CONNECTION);

		resizeEvent(0);
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::setupNetwork(const QString& serverName) {
		ui.errorMessage->setVisible(false);

		// Try to connect to server
		network = new Network(serverName, this);
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::jsonRequest(const QString& command, JSONRequest* requestData) {
		ui.indicator->setVisible(true);
		indicator->setPaused(false);
		network->requestJSON(command, requestData);
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::jsonResult(const QVariant& data, JSONRequestType type) {
		if(type == JSON_CURRENT_GAMES || type == JSON_GET_WORDLISTS) {
			widgets[activeWidget]->receiveJSON(type, data);
		} else {
			if(data.toMap()["success"].toBool()) {
				widgets[activeWidget]->receiveJSON(type, data);
			} else {
				reportError(tr("JSON Error: %1").arg(data.toMap()["error"].toString()));
			}
		}

		indicator->setPaused(true);
		ui.indicator->setVisible(false);
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::setToken(const QString& token) {
		this->playerToken = token;
	}

	// -------------------------------------------------------------------------
	const QString& BingoMainWindow::getToken() const {
		return playerToken;
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::setNick(const QString& nick) {
		this->nickname = nick;
	}

	// -------------------------------------------------------------------------
	const QString& BingoMainWindow::getNick() const {
		return this->nickname;
	}
	// -------------------------------------------------------------------------
	void BingoMainWindow::loadServerList(QString file) {
		QFile data(file);
		if (data.open(QFile::ReadOnly | QIODevice::Text)) {			
			QTextStream in(&data);
			QString line;

			do {
				line = in.readLine();
				if(!line.isEmpty()) {
					servers.push_back(line);
				}
			} while (!line.isNull());
		} else {
			this->reportError(tr("Unable to read servers.list. Defaulting to localhost:8000"));
		}

		if(servers.isEmpty()) {	
			servers.push_back("localhost:8000");
		}
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::setActiveWidget(int widget) {
		ui.errorMessage->setVisible(false);

		widgets[activeWidget]->hide();
		widgets[activeWidget]->deactivate();	
		
		activeWidget = widget;
		
		widgets[activeWidget]->show();
		widgets[activeWidget]->activate();
		resizeEvent(0);
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::setCurrentGame(const QString& id, const QString& name, 
		QList<QString> words, int size) {
		// Copy data to current Game information
		currentGame.id = id;
		currentGame.words = words;
		currentGame.name = name;
		currentGame.size = size;
	}

	// -------------------------------------------------------------------------
	const GameData& BingoMainWindow::getCurrentGame() const {
		return currentGame;
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::reportError(const QString& msg) {
		if(msg.length() > 80) {
			QMessageBox::critical(this,tr("Bingo"),msg,QMessageBox::Ok);
		} else {
			ui.indicator->setVisible(false);
			ui.errorMessage->setVisible(true);
			ui.errorMessage->setText(msg.isEmpty() ? tr("Unknown Error occurred.") : msg);

			if(msg.compare(tr("Network error")) == 0) {
				setActiveWidget(WIDGET_CONNECTION);
			}
		}
	}

	// -------------------------------------------------------------------------
	QSize BingoMainWindow::getContainerSize() {
		return ui.container->size();
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::notify(const QString& msg) {
		QMessageBox::information(this,tr("Bingo"),msg,QMessageBox::Ok);
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::resizeEvent(QResizeEvent * evt) {
		widgets[activeWidget]->resize(ui.container->size().width(),ui.container->size().height());
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::closeEvent(QCloseEvent *event) {
		// We can catch the event here to annoy the user, asking
		// if he really wants to quit.
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::menuExit() {
		qApp->exit(EXIT_SUCCESS);
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::menuAbout(){
		QMessageBox::aboutQt(this);
	}
	
	// -------------------------------------------------------------------------
	void BingoMainWindow::menuWebsite() {
		QDesktopServices::openUrl(QUrl("http://www.github.com/mstap/vsy-bullshit"));
	}

	// -------------------------------------------------------------------------
	void BingoMainWindow::menuDropToken() {
		int ret = QMessageBox::question(this,tr("Bingo"),
			tr("Drop Token <%1> locally?\nYour username will be unavailable to anyone until the token expires (24h).").arg(playerToken),
			QMessageBox::Yes, QMessageBox::No);
		if(ret == QMessageBox::Yes) {
			this->playerToken.clear();
			this->setActiveWidget(WIDGET_CONNECTION);
		}
	}
}
