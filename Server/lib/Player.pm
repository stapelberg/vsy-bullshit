# vim:ts=4:sw=4:expandtab
package Player;

use strict;
use Moose;
use Digest::SHA1 qw(sha1_hex);

has 'nickname' => (is => 'ro', isa => 'Str', required => 1);
has 'token' => (is => 'ro', isa => 'Str', builder => 'build_token');

sub build_token {
    my @random_values;
    push @random_values, rand(255) for (1..10);
    return sha1_hex(join('', @random_values));
}

__PACKAGE__->meta->make_immutable;

1
