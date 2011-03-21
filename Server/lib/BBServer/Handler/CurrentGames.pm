# vim:ts=4:sw=4:expandtab
package BBServer::Handler::CurrentGames;

use strict;
use parent qw(BBServer::Handler::Base);
use JSON::XS;
use Data::Dumper;
use Games;
use v5.10;
our $VERSION = '0.01';
__PACKAGE__->asynchronous(1);

sub describe_game {
    my ($game) = @_;

    return {
        id => $_->id,
        participants => [ $_->participants ],
        name => $_->name,
        created => $_->created
    };
}

sub handle_request {
    my ($self, $request) = @_;

    return [ map { describe_game($_) } Games->instance->games ];
}

1
