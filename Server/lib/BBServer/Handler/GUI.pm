# vim:ts=4:sw=4:expandtab
package BBServer::Handler::GUI;

use strict;
use parent qw(Tatsumaki::Handler);
use JSON::XS;
use Data::Dumper;
use Game;
use Games;
use IO::All;
use v5.10;
our $VERSION = '0.01';
__PACKAGE__->asynchronous(1);

sub get {
    my ($self, $query) = @_;
    
    my $contents = io('AJAXGUI/' . $query)->utf8->slurp;
    $self->write($contents);
    $self->finish;
}

1
