# vim:ts=4:sw=4:expandtab
package Players;

use strict;
use Moose;
use MooseX::Singleton;

has '_players' => (
    traits => ['Array'],
    is => 'ro',
    isa => 'ArrayRef',
    default => sub { [] },
    handles => {
        players => 'elements',
        add_player => 'push',
    }
);

sub exists {
    my ($self, $name) = @_;

    my @matches = grep { $_->nickname eq $name } @{$self->_players};
    return (@matches > 0);
}

sub by_token {
    my ($self, $token) = @_;

    my @matches = grep { $_->token eq $token } @{$self->_players};
    return (@matches > 0 ? $matches[0] : undef);
}

__PACKAGE__->meta->make_immutable;

1
