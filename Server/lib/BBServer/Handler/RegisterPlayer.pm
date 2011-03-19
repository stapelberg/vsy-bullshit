# vim:ts=4:sw=4:expandtab
package BBServer::Handler::RegisterPlayer;

use strict;
use parent qw(BBServer::Handler::Base);
use JSON::XS;
use v5.10;
use Data::Dumper;
use Players;
use Player;
our $VERSION = '0.01';
__PACKAGE__->asynchronous(1);

sub handle_request {
    my ($self, $request) = @_;

    return $self->error('No nickname provided') unless exists($request->{nickname});

    my $players = Players->new;
    return $self->error('Player already exists') if $players->exists($request->{nickname});

    my $player = Player->new(nickname => $request->{nickname});
    $players->add_player($player);
    return { token => $player->token };
}

1
