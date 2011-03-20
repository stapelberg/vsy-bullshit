/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#ifndef _JSONRequests_h_ 
#define _JSONRequests_h_

/* -----------------------------------------------------------------------------
 * We're storing all requests here, since I'm too lazy to create source files for 
 * each class, mainly because the implementations are quite trivial and compile
 * times do not really matter... 
 */

#include <QObject>
#include <QByteArray>
#include <QVariant>
#include "JSONRequestTypes.h"

// -----------------------------------------------------------------------------
// Base Class
class JSONRequest : public QObject {
	Q_OBJECT

public:
	JSONRequest(QObject* parent = 0) : QObject(parent) {}
	virtual ~JSONRequest() {};

	virtual JSONRequestType getType() = 0;
	virtual bool get() { return false; };
};
// -----------------------------------------------------------------------------
// List Current Games
class JSONCurrentGames : public JSONRequest {
		JSONRequestType getType() { return JSON_CURRENT_GAMES; }
		bool get() { return true; }
};

// -----------------------------------------------------------------------------
// Register Player Request
class JSONRegisterPlayer : public JSONRequest {

	Q_OBJECT

	Q_PROPERTY(QString nickname READ nickname WRITE setNickname)

public: 
	JSONRegisterPlayer(QObject* parent = 0) : JSONRequest(parent) {}
	~JSONRegisterPlayer() {}

	QString nickname() const { return name; };
	void setNickname(const QString& name) { this->name = name;}
	JSONRequestType getType() { return JSON_REGISTER_PLAYER; }

private:
	QString name;

};

// -----------------------------------------------------------------------------
// Create New Game
class JSONCreateGame : public JSONRequest {

		Q_OBJECT

		Q_PROPERTY(QString token READ token WRITE setToken)
		Q_PROPERTY(QString size READ size WRITE setSize)
public:

		JSONCreateGame(QObject* parent = 0) : JSONRequest(parent) {}
		~JSONCreateGame() {}

		QString token() const { return _token; };
		void setToken(const QString& token) { this->_token = token;}

		QString size() const { return _size; };
		void setSize(const QString& size) { this->_size = size;}

		JSONRequestType getType() { return JSON_CREATE_GAME; }
private:
	QString _token;
	QString _size;

};

#endif