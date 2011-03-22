// vim:ts=4:sw=4:expandtab

function register_player(nick) {
    $.post(
    '/RegisterPlayer',
    JSON.stringify({ nickname: nick }),
    function(data) {
        if (!data['success']) {
            // TODO: zurückleiten
            alert('unsuceessful: ' + data['error']);
        } else {
            // TODO: merge?
            $.bbq.pushState('#token=' + data['token']);
        }
    }
    );
};

function join_game(token, game) {
    $.post(
    '/JoinGame',
    JSON.stringify({ token: token, id: game }),
    function(data) {
        if (!data['success']) {
            alert('gnaah: ' + data['error']);
        } else {
            setup_field(token, game, data);
        }
    }
    );
};

function click_field(token, game, cnt) {
    //alert('clicked on ' + $(this).attr('cnt'));
    $('body').append('click: ' + token + ', ' + game + ', ' + cnt + '<br>');
    $.post(
    '/MakeMove',
    JSON.stringify({ token: token, id: game, field: cnt }),
    function(data) {
        if (data['success']) {
            return;
        }

        alert('noes: ' + data['error']);
    }
    );
};

function checkWinner(token, game) {
    $.post(
    '/CheckWinner',
    '{"token":"' + token + '", "id":"' + game + '"}',
    function(data) {
        if (!data['success']) {
            $('body').append('error:' + data['error']);
            return;
        }
        if (!data['winner']) {
            window.setTimeout(function() {
                checkWinner(token, game);
            }, 1000);
            return;
        }

        // TODO: display winner
        $('body').append('winner:' + data['winner']);
    }
    );
};

function setup_field(token, game, data) {
    var table = $('<table></table>');
    var cnt = 0;
    for (row = 0; row < data['size']; row++) {
        var tr = $('<tr></tr>');
        for (col = 0; col < data['size']; col++) {
            var td = $('<td></td>')
                .append(data['words'][cnt])
                .attr('cnt', cnt)
                .click(function() {
                    click_field(token, game, $(this).attr('cnt'));
                });
            cnt++;
            tr.append(td);
        }
        table.append(tr);
    }
    $('body').append(table);
    window.setTimeout(function() {
        checkWinner(token, game);
    }, 1000);
    // TODO: timer für CurrentGames bauen und neue Spieler anzeigen
};

function join_game_link(game) {
    var chose_game_html = '\
    <li>\
    <strong><a href="" class="name"></a></strong><br>\
    Spieler: <span class="players"></span>\
    </li>\
    ';

    var href = $.param.fragment(location.href, '#game=' + game['id']);
    var cg = $(chose_game_html);
    cg.find(".name")
        .append(game['name'])
        .attr('href', href)
        .attr('id', game['id'])
    cg.find(".players")
        .append(game['participants'].join(', '));
    return cg;
};

function load_game_list() {
    // TODO: clear
    $.ajax({
        url: "/CurrentGames",
        context: document.body,
        success: function(data) {
            var gameslist = $('#currentgames');
            data.map(join_game_link).forEach(function(game) {
                gameslist.append(game);
            });

            $("#status").replaceWith('<div id="status"><h1>Bullshit Bingo</h1></div>');
        }
    });
};
$(document).ready(function() {

    $('#submit').click(function() {
        $(this).attr('href', '#nick=wat');
    });

    $('#test').click(function() {
        var data = {
            words: [ 'B 2 B', 'User generated content', 'Use Case', 'Return of Investment', 'Web 2.0', 'Win-Win', 'Business Intelligence', 'Time to market', 'Wow factor' ],
            size: 3
        };
        setup_field('foo', 'bar', data);
    });


    //$("#status").replaceWith('<div id="status">Loading games...</div>');

    $(window).bind('hashchange', function(e) {
        var nick = $.bbq.getState('nick');
        var token = $.bbq.getState('token');
        // If neither nick nor token is set, display login form
        if (!nick && !token) {
            $('#currentgames').css('display', 'none');
            $('#login').css('display', 'block');
            return;
        }
        $('#status').replaceWith('<div id="status">hashchange: ' + nick + '</div>');
        $('#login').css('display', 'none');

        // If nick is set but token isn't, register the player
        if (nick && !token) {
            $('#status').replaceWith('<div id="status">should register: ' + nick + '</div>');
            register_player(nick);
            return;
        }

        // If game isn't set, display the list of games
        var game = $.bbq.getState('game');
        if (!game) {
            $('#currentgames').css('display', 'block');
            load_game_list();
            return;
        }

        $('#currentgames').css('display', 'none');
        $('#status').replaceWith('<div id="status">game: ' + game + '</div>');

        join_game(token, game);
    });

    $(window).trigger('hashchange');


});
