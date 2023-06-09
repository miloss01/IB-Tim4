export interface CertifDTO {
    serialNumber: number,
    startTime: string,
    endTime: string,
    subject: UserExpandedDTO,
    issuer: IssuerDTO,
    type: string,
    retracted: boolean
}

export interface UserExpandedDTO {
    name: string,
    lastName: string,
    email: string
    phone?: string
    password?: string
}

export interface IssuerDTO {
    serialNumber: number,
    type: string
}

export interface CertifRequestDTO {
    certificateType: string
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

export interface TwilioDTO {
    phone:string
    code:string
}

export interface PasswordReset {
    sender:string
    type:string
}

export interface PasswordChangeDTO {
    email: string
    password: string
    phone:string
    code:string
}
