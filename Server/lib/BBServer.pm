# vim:ts=4:sw=4:expandtab
package BBServer;

use strict;
use v5.10;
our $VERSION = '0.01';

use Tatsumaki::Application;
use Tatsumaki::Handler;

# Handlers
use BBServer::Handler::CurrentGames;
use BBServer::Handler::RegisterPlayer;
use BBServer::Handler::CreateGame;
use BBServer::Handler::JoinGame;
use BBServer::Handler::LeaveGame;
use BBServer::Handler::MakeMove;
use BBServer::Handler::CheckWinner;

sub webapp {
    my $class = shift;

    my $app = Tatsumaki::Application->new([
        '/CurrentGames'     => 'BBServer::Handler::CurrentGames',
        '/RegisterPlayer'   => 'BBServer::Handler::RegisterPlayer',
        '/CreateGame'       => 'BBServer::Handler::CreateGame',
        '/JoinGame'         => 'BBServer::Handler::JoinGame',
        '/LeaveGame'        => 'BBServer::Handler::LeaveGame',
        '/MakeMove'         => 'BBServer::Handler::MakeMove',
        '/CheckWinner'      => 'BBServer::Handler::CheckWinner',
    ]);

    $app->psgi_app;
}

1
