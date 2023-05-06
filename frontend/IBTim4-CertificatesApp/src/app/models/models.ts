export interface CertifDTO {
    id: number,
    startTime: string,
    endTime: string,
    subject: UserExpandedDTO,
    issuer: IssuerDTO,
    type: string
}

export interface UserExpandedDTO {
    name: string,
    lastName: string,
    email: string
}

export interface IssuerDTO {
    serialNumber: number,
    type: string
}

export interface CertifRequestDTO {
    type: string
    issuerSN: string,
    requesterEmail: string,
    expirationTime: string,
}

export interface CertificateRequestDTO {
    id: string,
    certificateType: string
    issuerSN: string,
    requesterEmail: string,
    status: string,
    description: string,
    creationTime: string,
    expirationTime: string,
}