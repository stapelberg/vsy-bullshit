# vim:ts=4:sw=4:expandtab
package BBServer::Handler::CreateGame;

use strict;
use parent qw(BBServer::Handler::Base);
use JSON::XS;
use Data::Dumper;
use Game;
use Games;
use v5.10;
our $VERSION = '0.01';
__PACKAGE__->asynchronous(1);

sub handle_request {
    my ($self, $request, $token) = @_;

    return $self->error('No token') unless defined($token);
    return $self->error('Game size missing') unless exists $request->{size};
    return $self->error('Game size invalid') unless $request->{size} =~ /^[0-9]+$/;

    my $player = Players->instance->by_token($token);

    my $game = Game->new(size => $request->{size});
    $game->add_player($player);

    Games->instance->add_game($game);

    return {
        id => $game->id,
        words => $game->words
    };
}

1
