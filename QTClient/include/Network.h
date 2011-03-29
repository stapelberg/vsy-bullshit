/*
* VSY Bingo QT Client 
* -----------------------------------------------------------------------------
* (c) 2011 Felix Bruckner. 
*
*  Licensed under the WTFPL.
* -----------------------------------------------------------------------------
*/
#ifndef _Network_h_
#define _Network_h_
#include "JSONRequests.h"
#include "JSONRequestTypes.h"

#include <QVariantMap>
#include <QUrl>
#include <QNetworkRequest>
#include <QNetworkReply>
#include <QReadWriteLock>

#include "serializer.h"

// Forward Decl
class QNetworkAccessManager;
class BingoMainWindow;

namespace Bingo {
	/**
	 * @brief Manages Network requests, namely JSON requests.
	 *
	 * This class uses the QNetworkAccessManager, so it is capable of 
	 * processing simultaneous JSON Requests, since every Request has its own
	 * thread.
	 */
	class Network : public QObject {
		Q_OBJECT

	public:
		Network(const QString& serverAddress, BingoMainWindow* parent);
		~Network();

		/**
		* Request JSON Data with the given request and POST Data to use.
		*/
		void requestJSON(const QString& request, JSONRequest* data);

		/**
		* Can be used to check whether the Network Manager is currently busy 
		* waiting for a reply
		*/
		bool requestInProgress();

		const QString& getServerAddress() const {
			return address;
		}
	private slots:
		void replyFinished(QNetworkReply*);
		void slotError(QNetworkReply::NetworkError error);

	private:
		QString address;
		BingoMainWindow *parent;
		QNetworkAccessManager *manager;
		QJson::Serializer serializer;
		QReadWriteLock lock;

		QMap<QString, JSONRequestType> requests;

		bool busy;

	};

}
#endif