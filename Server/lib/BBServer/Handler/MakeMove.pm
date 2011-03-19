# vim:ts=4:sw=4:expandtab
package BBServer::Handler::MakeMove;

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
    return $self->error('Game id missing') unless exists $request->{id};
    return $self->error('Field number missing') unless exists $request->{field};
    return $self->error('Field number invalid') unless $request->{field} =~ /^[0-9]+$/;

    my $player = Players->instance->by_token($token);
    return $self->error('No such token') unless defined($player);

    my $game = Games->instance->by_id($request->{id});
    return $self->error('No such game') unless defined($game);

    $game->make_move($player, $request->{field});

    return {};
}

1
