# vim:ts=4:sw=4:expandtab
package BBServer::Handler::GetWordlists;

use strict;
use parent qw(BBServer::Handler::Base);
use JSON::XS;
use Data::Dumper;
use Games;
use v5.10;
our $VERSION = '0.01';
__PACKAGE__->asynchronous(1);

sub handle_request {
    my ($self, $request) = @_;

    my @lists = map { ($_) =~ m,^wordlists/(.*)\.txt$, } <wordlists/*.txt>;
    \@lists
}

1
