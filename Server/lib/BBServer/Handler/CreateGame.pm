# vim:ts=4:sw=4:expandtab
package BBServer::Handler::CreateGame;

use strict;
use parent qw(BBServer::Handler::Base);
use JSON::XS;
use Data::Dumper;
use v5.10;
our $VERSION = '0.01';
__PACKAGE__->asynchronous(1);

sub handle_request {
    my ($self, $request, $token) = @_;

    return $self->error('No token') unless defined($token);

    my $player = Players->instance->by_token($token);

    return { yo => 'dawg' };
}

1
