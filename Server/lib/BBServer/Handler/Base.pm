# vim:ts=4:sw=4:expandtab
package BBServer::Handler::Base;

use strict;
use parent qw(Tatsumaki::Handler);
use JSON::XS;
use Try::Tiny;
use v5.10;
use Data::Dumper;
use Players;
use Player;
our $VERSION = '0.01';
__PACKAGE__->asynchronous(1);

sub error {
    my ($self, $msg) = @_;
    return {
        success => JSON::XS::false,
        error => $msg
    };
}

sub get {
    my ($self) = @_;

    my $result;
    try {
        $result = $self->handle_request;
    } catch {
        $result = $self->error('Error in handler: ' . $_);
    };

    $self->response->content_type('application/json');
    $self->write(encode_json($result));
    $self->finish;
}

sub post {
    my ($self) = @_;

    my $body;
    my $result;
    try {
        $body = decode_json($self->request->content);
        my $token = undef;
        $token = $body->{token} if exists $body->{token};
        try {
            $result = $self->handle_request($body, $token);
            if (not exists($result->{success})) {
                $result->{success} = JSON::XS::true;
                $result->{error} = '';
            }
        } catch {
            $result = $self->error('Error in handler: ' . $_);
        };
    } catch {
        $result = $self->error('No JSON provided');
    };

    $self->response->content_type('application/json');
    $self->write(encode_json($result));
    $self->finish;
}

1
