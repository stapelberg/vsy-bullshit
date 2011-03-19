# vim:ts=4:sw=4:expandtab
package BBServer::Handler::CurrentGames;

use strict;
use parent qw(Tatsumaki::Handler);
use JSON::XS;
use Data::Dumper;
use v5.10;
our $VERSION = '0.01';
__PACKAGE__->asynchronous(1);

sub get {
    my ($self) = @_;

    my $players = Players->new;

    $self->response->content_type('application/json');
    $self->write(Dumper($players->players));
    $self->finish;
}

1
