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

sub webapp {
    my $class = shift;

    my $app = Tatsumaki::Application->new([
        '/CurrentGames' => 'BBServer::Handler::CurrentGames',
        '/RegisterPlayer' => 'BBServer::Handler::RegisterPlayer',
        '/CreateGame' => 'BBServer::Handler::CreateGame',
    ]);

    $app->psgi_app;
}

1
