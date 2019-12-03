const chai = require('chai');
const chaiHttp = require('chai-http');

const app = require('../../Web_Dev/assignment1_comparison/coursework1_node');

chai.should();
chai.use(chaiHttp);

/* Test the /GET route */
describe('app index route', () => {
    it('it should GET /', (done) => {
        chai.request(app)
            .get('/')
            .end((err, res) => {
                res.should.have.status(200);
                done();
            });
    });

    it('it should handle 404 error', (done) => {
        chai.request(app)
            .get('/notExist')
            .end((err, res) => {
                res.should.have.status(404);
                done();
            });
    });
});

/* Test the /GET route */
describe('movies', () => {
    // describe('get by id', () => {
        it('it should GET /id', (done) => {
            chai.request(app)
                .get('/movies/63')
                .end((err, res) => {
                    console.info(res);
                    res.should.not.have.status(200);
                    done();
                });
        });

        it('it should handle 404 error', (done) => {
            chai.request(app)
                .get('/movies/notExist')
                .end((err, res) => {
                    res.should.have.status(404);
                    done();
                });
        });
    // });
});
