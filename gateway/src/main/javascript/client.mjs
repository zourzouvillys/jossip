import grpc from '@grpc/grpc-js';
import protoLoader from '@grpc/proto-loader';

const packageDefinition = protoLoader.loadSync(
    '../proto/gateway.proto',
    {
        keepCase: false,
        longs: String,
        enums: String,
        defaults: true,
        oneofs: true
    }
);

const gateway = grpc.loadPackageDefinition(packageDefinition).rtcore.gateway.v1;

export default class SipClient {

    constructor(server) {
        this.client = new gateway.SipServer(server, grpc.credentials.createInsecure());
    }

    async exchange(connectionId, frame) {

        return await new Promise((resolve, reject) => {

            var call = this.client.Exchange({ connectionId, frame, });

            let message = null;

            call.on('data', data => message = data);

            call.on('end', function () {
                console.log('END');
            });

            call.on('error', function (e) {
                // An error has occurred and the stream has been closed.
                reject(e);
            });

            call.on('status', function (status) {
                if (status.code !== 0 || status.details !== '') {
                    console.log("STATUS", status, message);
                }
                resolve(message);
            });

            // call.write({ name });
            // call.end();

        });

    }


}

