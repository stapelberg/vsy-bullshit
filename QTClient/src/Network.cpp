/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#include <QNetworkAccessManager>
#include <QVariantMap>
#include <QMap>
#include <QMessageBox>

#include "BingoMainWindow.h"
#include "Network.h"

#include "qobjecthelper.h"
#include "serializer.h"
#include "parser.h"

#include <sstream>

namespace Bingo {

	// -------------------------------------------------------------------------
	inline QString _addr(QNetworkReply* obj) {
		std::stringstream _address; 
		_address << obj; 
		return QString(_address.str().c_str());

	}

	// -------------------------------------------------------------------------
	Network::Network(const QString& serverAddress, BingoMainWindow* parent)
		: address(serverAddress), 
		parent(parent),
		manager(new QNetworkAccessManager(this)),
		busy(false) 
	{
		connect(manager, SIGNAL(finished(QNetworkReply*)), this, SLOT(replyFinished(QNetworkReply*)));
	}

	// -------------------------------------------------------------------------
	Network::~Network() {

	}

	// -------------------------------------------------------------------------
	void Network::requestJSON(const QString& command, JSONRequest* data) {
		busy = true;

		// Form Request header
		QNetworkRequest request;
		request.setUrl(QUrl("http://"+address+"/"+command));
		request.setRawHeader("User-Agent", "VSY QTBingoClient");

		QNetworkReply* reply;

		if(data->get()) {
			reply = manager->get(request);
		} else {
			QVariantMap variant = QJson::QObjectHelper::qobject2qvariant(data);
			reply = manager->post(request, serializer.serialize(variant));
		}

		 // Map Object address to request type
		requests[_addr(reply)] =  data->getType();

		 connect(reply, SIGNAL(error(QNetworkReply::NetworkError)),
			 this, SLOT(slotError(QNetworkReply::NetworkError)));

	}

	// -------------------------------------------------------------------------
	void Network::replyFinished(QNetworkReply* reply) {
	//    lock.lockForWrite();
		
		QJson::Parser parser;
		bool ok;
		QVariant result = parser.parse (reply->readAll(), &ok);

		if(ok) {
			parent->jsonResult(result, requests[_addr(reply)]);
		} else {
			parent->reportError("Parser Error");
		}

		requests.remove(_addr(reply));
		
		reply->deleteLater();
		busy = false;
	//	lock.unlock();
	}


	// -------------------------------------------------------------------------
	void Network::slotError(QNetworkReply::NetworkError error) {
		parent->reportError("Network error");
	}

	// -------------------------------------------------------------------------
	bool Network::requestInProgress() {
		return busy;
	}


}
